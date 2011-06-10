package info.pppc.pcom.swtui.application.builder;

/**
 * Constant definitions for the application model visualized in the
 * tree view of the application buidler.
 * 
 * @author Mac
 */
public interface IApplicationModel {

	/**
	 * The root node that contains the descriptor.
	 */
	public static final int TYPE_DESCRIPTOR = 1;
	
	/**
	 * The node that contains the application identifier, if any.
	 */
	public static final int TYPE_IDENTIFIER = 2;
	
	/**
	 * The node that contains the name of the application.
	 */
	public static final int TYPE_NAME = 3;
	
	/**
	 * The node that contains the object id of the assembler.
	 */
	public static final int TYPE_ASSEMBLER = 5;
	
	/**
	 * The node that contains the image of the descriptor.
	 */
	public static final int TYPE_IMAGE = 6;
	
	/**
	 * The node that contains the preference levels as contracts.
	 */
	public static final int TYPE_CONTRACTS = 7;
	
	/**
	 * The type for a single contract. The data object will be the
	 * contract itself.
	 */
	public static final int TYPE_CONTRACT = 8;
	
	/**
	 * Visualizes some attribute of a contract. The data object will
	 * be the byte that represents the attribute.
	 */
	public static final int TYPE_ATTRIBUTE = 9;

}
