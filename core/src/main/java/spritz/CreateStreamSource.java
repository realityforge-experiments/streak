package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class CreateStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final SourceCreator<T> _createFunction;

  CreateStreamSource( @Nullable final String name, @Nonnull final SourceCreator<T> createFunction )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "create" ) : null );
    _createFunction = Objects.requireNonNull( createFunction );
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final PassThroughSubscription<T, CreateStreamSource<T>> subscription =
      new PassThroughSubscription<>( this, subscriber );
    subscription.setUpstream( subscription );
    subscriber.onSubscribe( subscription );
    _createFunction.create( new SimpleSubscriberAdapter<>( subscriber, subscription ) );
    return subscription;
  }

  private static class SimpleSubscriberAdapter<T>
    implements EventEmitter<T>
  {
    @Nonnull
    private final Subscriber<? super T> _subscriber;
    @Nonnull
    private final PassThroughSubscription<T, CreateStreamSource<T>> _subscription;
    private boolean _done;

    SimpleSubscriberAdapter( @Nonnull final Subscriber<? super T> subscriber,
                             @Nonnull final PassThroughSubscription<T, CreateStreamSource<T>> subscription )
    {
      _subscriber = subscriber;
      _subscription = subscription;
    }

    @Override
    public void next( @Nonnull final T item )
    {
      if ( isNotDone() )
      {
        _subscriber.onItem( item );
      }
    }

    @Override
    public void error( @Nonnull final Throwable throwable )
    {
      if ( isNotDone() )
      {
        _subscriber.onError( throwable );
        _done = true;
      }
    }

    @Override
    public void complete()
    {
      if ( isNotDone() )
      {
        _subscriber.onComplete();
        _done = true;
      }
    }

    @Override
    public boolean isDone()
    {
      return _done || _subscription.isDone();
    }
  }
}
