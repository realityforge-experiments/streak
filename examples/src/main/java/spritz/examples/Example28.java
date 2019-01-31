package spritz.examples;

import spritz.Stream;
import spritz.schedulers.Schedulers;

public class Example28
{
  public static void main( String[] args )
  {
    Stream
      .periodic( 50 )
      .filter( v -> !( v > 10 && v < 20 ) )
      .sample( 210 )
      .take( 12 )
      .afterTerminate( Example28::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}