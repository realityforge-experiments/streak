package spritz;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

final class PredicateFilterStream<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Predicate<? super T> _predicate;

  PredicateFilterStream( @Nonnull final Stream<? extends T> upstream,
                         @Nonnull final Predicate<? super T> predicate )
  {
    super( upstream );
    _predicate = Objects.requireNonNull( predicate );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _predicate ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T>
  {
    @Nonnull
    private final Predicate<? super T> _predicate;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber,
                        @Nonnull final Predicate<? super T> predicate )
    {
      super( subscriber );
      _predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldIncludeItem( @Nonnull final T item )
    {
      return _predicate.test( item );
    }
  }
}