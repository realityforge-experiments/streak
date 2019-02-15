package spritz;

import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nonnull;
import static org.realityforge.braincheck.Guards.*;

final class ValidatingSubscriber<T>
  implements Subscriber<T>
{
  private static final ArrayList<ValidatingSubscriber<?>> c_subscriberContext = new ArrayList<>();

  enum State
  {
    CREATED,
    SUBSCRIBE_STARTED,
    SUBSCRIBE_COMPLETED,
    ERRORED,
    COMPLETED
  }

  @Nonnull
  private final Subscriber<T> _target;
  @Nonnull
  private State _state;

  ValidatingSubscriber( @Nonnull final Subscriber<T> target )
  {
    _target = Objects.requireNonNull( target );
    _state = State.CREATED;
  }

  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> State.CREATED == _state,
                 () -> "Spritz-0001: Subscriber.onSubscribe(...) called and expected state " +
                       "to be CREATED but is " + _state );
      Objects.requireNonNull( subscription );
    }
    try
    {
      pushContext( this );
      _state = State.SUBSCRIBE_STARTED;
      _target.onSubscribe( new WorkerSubscription<>( this, subscription ) );
      _state = State.SUBSCRIBE_COMPLETED;
    }
    catch ( final Throwable throwable )
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        fail( () -> "Spritz-0003: Invoking Subscriber.onSubscribe(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( throwable ) );
      }
      throw throwable;
    }
    finally
    {
      popContext( this );
    }
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> State.SUBSCRIBE_COMPLETED == _state,
                 () -> "Spritz-0005: Subscriber.onNext(...) called and expected state " +
                       "to be SUBSCRIBED but is " + _state );
      Objects.requireNonNull( item );
    }

    try
    {
      pushContext( this );
      _target.onNext( item );
    }
    catch ( final Throwable throwable )
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        fail( () -> "Spritz-0004: Invoking Subscriber.onNext(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( throwable ) );
      }
      throw throwable;
    }
    finally
    {
      popContext( this );
    }
  }

  @Override
  public void onError( @Nonnull final Throwable error )
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> State.SUBSCRIBE_COMPLETED == _state,
                 () -> "Spritz-0006: Subscriber.onError(...) called and expected state " +
                       "to be SUBSCRIBED but is " + _state );
      Objects.requireNonNull( error );
    }
    try
    {
      pushContext( this );
      _state = State.ERRORED;
      _target.onError( error );
    }
    catch ( final Throwable t )
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        fail( () -> "Spritz-0007: Invoking Subscriber.onError(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( t ) );
      }
      throw t;
    }
    finally
    {
      popContext( this );
    }
  }

  @Override
  public void onComplete()
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> State.SUBSCRIBE_COMPLETED == _state,
                 () -> "Spritz-0008: Subscriber.onComplete(...) called and expected state " +
                       "to be SUBSCRIBED but is " + _state );
    }

    try
    {
      pushContext( this );
      _state = State.COMPLETED;
      _target.onComplete();
    }
    catch ( final Throwable t )
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        fail( () -> "Spritz-0009: Invoking Subscriber.onComplete(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( t ) );
      }
      throw t;
    }
    finally
    {
      popContext( this );
    }
  }

  private static boolean hasContext()
  {
    return !c_subscriberContext.isEmpty();
  }

  @Nonnull
  private static ValidatingSubscriber<?> currentContext()
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( ValidatingSubscriber::hasContext,
                 () -> "Spritz-0012: Invoking Subscriber.currentContext(...) but no subscriber on stack." );
    }
    final int index = c_subscriberContext.size() - 1;
    return c_subscriberContext.remove( index );
  }

  private static void pushContext( @Nonnull final ValidatingSubscriber<?> subscriber )
  {
    c_subscriberContext.add( Objects.requireNonNull( subscriber ) );
  }

  private static void popContext( @Nonnull final ValidatingSubscriber<?> subscriber )
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> !c_subscriberContext.isEmpty(),
                 () -> "Spritz-0010: Invoking Subscriber.popContext(...) but no subscriber on stack. " +
                       "Expecting subscriber: " + subscriber );
    }
    final int index = c_subscriberContext.size() - 1;
    final ValidatingSubscriber<?> removed = c_subscriberContext.remove( index );
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> removed == subscriber,
                 () -> "Spritz-0011: Invoking Subscriber.popContext(...) popped subscriber '" + removed +
                       "' but was expecting subscriber '" + subscriber + "'." );
    }
  }

  private static final class WorkerSubscription<T>
    implements Subscription
  {
    @Nonnull
    private final ValidatingSubscriber<T> _subscriber;
    @Nonnull
    private final Subscription _subscription;
    private boolean _cancelled;

    WorkerSubscription( @Nonnull final ValidatingSubscriber<T> subscriber,
                        @Nonnull final Subscription subscription )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _subscription = Objects.requireNonNull( subscription );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        invariant( ValidatingSubscriber::hasContext,
                   () -> "Spritz-0018: Invoking Subscription.cancel(...) but not in the context of a subscriber." );
        final ValidatingSubscriber<?> subscriber = currentContext();
        invariant( () -> subscriber == _subscriber,
                   () -> "Spritz-0019: Invoking Subscription.cancel(...) in the context of subscriber '" + subscriber +
                         "' but expected to be in the context of subscriber '" + _subscriber + "'." );
        invariant( () -> !_cancelled,
                   () -> "Spritz-0013: Invoking Subscription.cancel(...) for subscriber '" + subscriber +
                         "' but the subscription is already cancelled." );
      }
      if ( !_cancelled )
      {
        try
        {
          _cancelled = true;
          _subscription.cancel();
        }
        catch ( final Throwable t )
        {
          if ( Spritz.shouldCheckInvariants() )
          {
            fail( () -> "Spritz-0020: Invoking Subscription.cancel(...) incorrectly threw an exception. " +
                        "Exception:\n" + ErrorUtil.throwableToString( t ) );
          }
          throw t;
        }
      }
    }
  }
}
