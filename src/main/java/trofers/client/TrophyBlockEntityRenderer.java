package trofers.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import trofers.common.TrophyBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class TrophyBlockEntityRenderer extends TileEntityRenderer<TrophyBlockEntity> {

    public TrophyBlockEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TrophyBlockEntity trophy, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        matrixStack.pushPose();

        matrixStack.translate(0.5, 0, 0.5);

        Direction direction = trophy.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-direction.toYRot()));

        render(trophy.getItem(), trophy.getDisplayHeight(), trophy.getDisplayScale(), matrixStack, buffer, light, overlay);

        matrixStack.popPose();
    }

    protected static void render(ItemStack displayItem, float displayHeight, float displayScale, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        IBakedModel model = renderer.getModel(displayItem, Minecraft.getInstance().level, null);

        matrixStack.translate(0, displayHeight / 16D + 0.25, 0);

        matrixStack.translate(0, -0.25, 0);
        matrixStack.scale(displayScale, displayScale, displayScale);
        if (!model.isGui3d()) {
            matrixStack.scale(4/3F, 4/3F, 4/3F);
        }
        matrixStack.translate(0, 0.25, 0);
        if (!model.isGui3d()) {
            matrixStack.scale(0.5F, 0.5F, 0.5F);
        }

        Minecraft.getInstance().getItemRenderer().renderStatic(displayItem, ItemCameraTransforms.TransformType.FIXED, light, overlay, matrixStack, buffer);
    }
}
