package spritz;

import spritz.internal.annotations.GwtIncompatible;

/**
 * Location of all compile time configuration settings for framework.
 */
final class SpritzConfig
{
  private static final ConfigProvider PROVIDER = new ConfigProvider();
  private static final boolean PRODUCTION_MODE = PROVIDER.isProductionMode();
  private static boolean VALIDATE_SUBSCRIPTIONS = PROVIDER.shouldValidateSubscriptions();

  private SpritzConfig()
  {
  }

  static boolean isProductionMode()
  {
    return PRODUCTION_MODE;
  }

  static boolean shouldValidateSubscriptions()
  {
    return VALIDATE_SUBSCRIPTIONS;
  }

  private static final class ConfigProvider
    extends AbstractConfigProvider
  {
    @GwtIncompatible
    @Override
    boolean isProductionMode()
    {
      return "production".equals( System.getProperty( "spritz.environment", "production" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldValidateSubscriptions()
    {
      return "true".equals( System.getProperty( "spritz.validate_subscriptions",
                                                isProductionMode() ? "false" : "true" ) );
    }
  }

  @SuppressWarnings( { "unused", "StringEquality" } )
  private static abstract class AbstractConfigProvider
  {
    boolean isProductionMode()
    {
      return "production" == System.getProperty( "spritz.environment" );
    }

    boolean shouldValidateSubscriptions()
    {
      return "true" == System.getProperty( "spritz.validate_subscriptions" );
    }
  }
}