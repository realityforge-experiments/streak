package streak;

import javax.annotation.Nonnull;

final class NeverStreamSource<T>
  implements Stream<T>
{
  NeverStreamSource()
  {
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    subscriber.onSubscribe( new WorkerSubscription<T>() );
  }

  private static final class WorkerSubscription<T>
    implements Subscription
  {
    private WorkerSubscription()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
    }
  }
}
