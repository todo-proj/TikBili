package com.benyq.tikbili.scene.horizontal.layer

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.benyq.tikbili.scene.Layers

/**
 *
 * @author benyq
 * @date 4/26/2024
 *
 */
class SpeedSelectDialogLayer: DialogListLayer<Float>() {
    override fun backPressedPriority() = Layers.BackPriority.SPEED_SELECT_DIALOG_LAYER_BACK_PRIORITY

    override val tag: String = "SpeedSelectDialogLayer"

    init {
        adapter().setItemAction { position, item->
            val player = player() ?: return@setItemAction
            val speed = player.getSpeed()
            if (speed != item) {
                player.setSpeed(item)
                animateDismiss()
                adapter().setSelected(adapter().findItem(item))
            }
        }
    }

    override fun onCreateLayerView(parent: ViewGroup): View? {
        setTitle("Speed")
        adapter().setList(createItems(parent.context))
        return super.onCreateLayerView(parent)
    }

    override fun show() {
        super.show()
        val player = player() ?: return
        adapter().setSelected(adapter().findItem(player.getSpeed()))
    }

    private fun createItems(context: Context): List<Item<Float>> {
        val items: MutableList<Item<Float>> = ArrayList()
        items.add(Item(0.5f, "0.5x"))
        items.add(Item(0.75f, "0.75x"))
        items.add(Item(1f, "1.0x" + "(Default)"))
        items.add(Item(1.25f, "1.25x"))
        items.add(Item(1.5f, "1.5x"))
        items.add(Item(2.0f, "2.0x"))
        items.reverse()
        return items
    }

}