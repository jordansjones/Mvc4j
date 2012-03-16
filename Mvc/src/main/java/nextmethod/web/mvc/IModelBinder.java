package nextmethod.web.mvc;

public interface IModelBinder {

	Object bindModel(final ControllerContext controllerContext, final ModelBindingContext modelBindingContext);

}
