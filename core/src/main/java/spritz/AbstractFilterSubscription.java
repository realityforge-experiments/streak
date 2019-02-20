package spritz;

import javax.annotation.Nonnull;

abstract class AbstractFilterSubscription<T>
  extends PassThroughSubscription<T>
{
  AbstractFilterSubscription( @Nonnull final Subscriber<? super T> subscriber )
  {
    super( subscriber );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onNext( @Nonnull final T item )
  {
    if ( includeItem( item ) )
    {
      getDownstreamSubscriber().onNext( item );
    }
  }

  /**
   * Return true if item should be included in stream.
   * This method will catch any exception generated by the custom code that tests whether to include
   * item and convert it to an onError signal.
   *
   * @return true if item should be included in stream.
   */
  private boolean includeItem( @Nonnull final T item )
  {
    try
    {
      return shouldIncludeItem( item );
    }
    catch ( final Throwable throwable )
    {
      onError( throwable );
      getUpstream().cancel();
      return false;
    }
  }

  /**
   * Return true if the specified item should be included in the subscription.
   *
   * @return true if the specified item should be included in the subscription.
   */
  protected abstract boolean shouldIncludeItem( @Nonnull T item );
}
