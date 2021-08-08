package rikka.librikka.model;


import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.math.Vector3f;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class ModelPerspectives {
    public final static ItemTransforms ItemBlock = new ItemTransforms(
    		//						Rotation					Translation						Scale
    		new ItemTransform(new Vector3f(75, 45, 0), 	new Vector3f(0, 0.15625F, 0), 	new Vector3f(0.375F, 0.375F, 0.375F)),	//thirdperson_leftIn
    		new ItemTransform(new Vector3f(75, 45, 0), 	new Vector3f(0, 0.15625F, 0), 	new Vector3f(0.375F, 0.375F, 0.375F)),	//thirdperson_rightIn
    		new ItemTransform(new Vector3f(0, 225, 0), 	new Vector3f(), 				new Vector3f(0.4F, 0.4F, 0.4F)),		//firstperson_leftIn
    		new ItemTransform(new Vector3f(0, 45, 0), 		new Vector3f(), 				new Vector3f(0.4F, 0.4F, 0.4F)),		//firstperson_rightIn
    		new ItemTransform(new Vector3f(), 				new Vector3f(), 				new Vector3f()), 						//headIn
            new ItemTransform(new Vector3f(30, 225, 0), 	new Vector3f(), 				new Vector3f(0.625F, 0.625F, 0.625F)), 	//guiIn
            new ItemTransform(new Vector3f(), 				new Vector3f(0, 0.1875F, 0), 	new Vector3f(0.25F, 0.25F, 0.25F)),		//groundIn
            new ItemTransform(new Vector3f(), 				new Vector3f(), 				new Vector3f(0.5F, 0.5F, 0.5F)));		//fixedIn
    
    public static ItemTransforms create(ItemTransforms parent,
    		ItemTransform thirdperson_leftIn, ItemTransform thirdperson_rightIn,
    		ItemTransform firstperson_leftIn, ItemTransform firstperson_rightIn,
    		ItemTransform headIn, ItemTransform guiIn, ItemTransform groundIn, ItemTransform fixedIn) {
    	
    	thirdperson_leftIn 	= thirdperson_leftIn==null	? parent.thirdPersonLeftHand 	: thirdperson_leftIn;
    	thirdperson_rightIn = thirdperson_rightIn==null	? parent.thirdPersonRightHand 	: thirdperson_rightIn;
    	firstperson_leftIn 	= firstperson_leftIn==null	? parent.firstPersonLeftHand 	: firstperson_leftIn;
    	firstperson_rightIn = firstperson_rightIn==null	? parent.firstPersonRightHand 	: firstperson_rightIn;
    	headIn 				= headIn==null				? parent.head 				: headIn;
    	guiIn 				= guiIn==null				? parent.gui 				: guiIn;
    	groundIn 			= groundIn==null			? parent.ground 			: groundIn;
    	fixedIn 			= fixedIn==null				? parent.fixed 				: fixedIn;
    	
    	return new ItemTransforms(thirdperson_leftIn, thirdperson_rightIn,
    			firstperson_leftIn, firstperson_rightIn, headIn, guiIn, groundIn, fixedIn);
    }
}
