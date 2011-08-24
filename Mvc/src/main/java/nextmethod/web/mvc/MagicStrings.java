package nextmethod.web.mvc;

/**
 *
 */
interface MagicStrings {

	String ActionKey = "action";
	String AreaKey = "area";
	String ControllerKey = "controller";
	String MvcVersionHeaderName = "X-Mvc4j-Version";
	String PackagesKey = "Packages";
	String UsePackageFallbackKey = "UsePackageFallback";

	String DefaultPackages = "controllers";
	String DefaultPackagesParamKey = "controllers.defaultPackages";
	String DefaultPackagesSeparator = ",";

	String UngroupedAssemblyName = "MVC4j-UngroupedAssemblyName-Classes";
	String ControllerTypeCacheName = "MVC-ControllerTypeCache.xml";
	String AreaTypeCacheName = "MVC-AreaRegistrationTypeCache.xml";
}
