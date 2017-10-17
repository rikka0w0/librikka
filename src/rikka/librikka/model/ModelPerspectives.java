package rikka.librikka.model;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPerspectives {
    public final static ItemCameraTransforms ItemBlock = new ItemCameraTransforms(
    		//						Rotation					Translation						Scale
    		new ItemTransformVec3f(new Vector3f(75, 45, 0), 	new Vector3f(0, 0.15625F, 0), 	new Vector3f(0.375F, 0.375F, 0.375F)),	//thirdperson_leftIn
    		new ItemTransformVec3f(new Vector3f(75, 45, 0), 	new Vector3f(0, 0.15625F, 0), 	new Vector3f(0.375F, 0.375F, 0.375F)),	//thirdperson_rightIn
    		new ItemTransformVec3f(new Vector3f(0, 225, 0), 	new Vector3f(), 				new Vector3f(0.4F, 0.4F, 0.4F)),		//firstperson_leftIn
    		new ItemTransformVec3f(new Vector3f(0, 45, 0), 		new Vector3f(), 				new Vector3f(0.4F, 0.4F, 0.4F)),		//firstperson_rightIn
    		new ItemTransformVec3f(new Vector3f(), 				new Vector3f(), 				new Vector3f()), 						//headIn
            new ItemTransformVec3f(new Vector3f(30, 225, 0), 	new Vector3f(), 				new Vector3f(0.625F, 0.625F, 0.625F)), 	//guiIn
            new ItemTransformVec3f(new Vector3f(), 				new Vector3f(0, 0.1875F, 0), 	new Vector3f(0.25F, 0.25F, 0.25F)),		//groundIn
            new ItemTransformVec3f(new Vector3f(), 				new Vector3f(), 				new Vector3f(0.5F, 0.5F, 0.5F)));		//fixedIn
    
    public static ItemCameraTransforms create(ItemCameraTransforms parent,
    		ItemTransformVec3f thirdperson_leftIn, ItemTransformVec3f thirdperson_rightIn,
    		ItemTransformVec3f firstperson_leftIn, ItemTransformVec3f firstperson_rightIn,
    		ItemTransformVec3f headIn, ItemTransformVec3f guiIn, ItemTransformVec3f groundIn, ItemTransformVec3f fixedIn) {
    	
    	thirdperson_leftIn 	= thirdperson_leftIn==null	? parent.thirdperson_left 	: thirdperson_leftIn;
    	thirdperson_rightIn = thirdperson_rightIn==null	? parent.thirdperson_right 	: thirdperson_rightIn;
    	firstperson_leftIn 	= firstperson_leftIn==null	? parent.firstperson_left 	: firstperson_leftIn;
    	firstperson_rightIn = firstperson_rightIn==null	? parent.firstperson_right 	: firstperson_rightIn;
    	headIn 				= headIn==null				? parent.head 				: headIn;
    	guiIn 				= guiIn==null				? parent.gui 				: guiIn;
    	groundIn 			= groundIn==null			? parent.ground 			: groundIn;
    	fixedIn 			= fixedIn==null				? parent.fixed 				: fixedIn;
    	
    	return new ItemCameraTransforms(thirdperson_leftIn, thirdperson_rightIn,
    			firstperson_leftIn, firstperson_rightIn, headIn, guiIn, groundIn, fixedIn);
    }
}
