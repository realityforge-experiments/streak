package streak.examples;

import streak.Streak;
import streak.schedulers.Schedulers;

public class Example27
{
  public static void main( String[] args )
  {
    Streak
      .periodic( 100 )
      .peek( v -> System.out.println( "Ping @ " + v ) )
      .throttleFirst( 150 )
      .peek( v -> System.out.println( "Ding @ " + v ) )
      .take( 5 )
      .afterTerminate( Example27::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( () -> Schedulers.current().shutdown() ).run();
  }
}