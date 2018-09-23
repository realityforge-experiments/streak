package streak;

public final class Streak
{
  private Streak()
  {
  }

  @SafeVarargs
  public static <T> Flow.Publisher<T> of( final T... values )
  {
    return new StaticPublisher<T>( values );
  }

  /**
   * Constructs a RangePublisher instance with the given start and count values
   * that yields a sequence of [start, start + count).
   *
   * @param start the starting value of the range
   * @param count the number of items to emit
   * @return the created publisher.
   */
  public static Flow.Publisher<Integer> range( final int start, final int count )
  {
    return new RangePublisher( start, count );
  }
}