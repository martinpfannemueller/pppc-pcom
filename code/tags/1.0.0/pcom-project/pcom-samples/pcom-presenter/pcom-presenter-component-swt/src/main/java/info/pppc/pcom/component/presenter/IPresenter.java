package info.pppc.pcom.component.presenter;

/**
 * This is a marker interface for the presenter application.
 * The presenter is a stateful component that supports 
 * checkpointing. The checkpoint contains the current presentation
 * and the current slide.
 * 
 * @author Mac
 */
public interface IPresenter {
	
	/**
	 * The identifier in the checkpoint used to denote the current
	 * presentation. This is either null or a byte array
	 * that represents a powerpoint file.
	 */
	public static final String CHECKPOINT_PRESENTATION = "component.presenter.presentation";
	
	/**
	 * The identifier in the checkpoint used to denote the current
	 * presentation. This is an integer value.
	 */
	public static final String CHECKPOINT_SLIDE = "component.presenter.slide";
	
}
