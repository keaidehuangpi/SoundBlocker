package top.huiwow

import gg.essential.universal.utils.MCClickEventAction
import gg.essential.universal.utils.MCHoverEventAction
import gg.essential.universal.wrappers.message.UMessage
import gg.essential.universal.wrappers.message.UTextComponent
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import top.huiwow.config.Config
import top.huiwow.utils.GenericSetJsonUtil


class SoundBlocker {
    var blockedSoundNames = HashSet<String>()
    var blockedSounds = HashSet<SoundProperty>()
    @SubscribeEvent
    fun onPacket(event: PlaySoundEvent) {
        if (!Config.enabled){
            return
        }
        if (!isPlayerInGame()){
            return
        }



        val sound = event.sound
        if (sound != null) {
            val name =event.name
            val pitch = sound.pitch

            val receivedSound = SoundProperty(name,pitch)
            var shouldGray : Boolean = false

            when(Config.blockSoundMode){
                0-> if (blockedSoundNames.contains(receivedSound.name)){
                    event.result = null
                    shouldGray=true
                }
                1-> if (blockedSounds.contains(receivedSound)){
                    event.result = null
                    event.isCanceled= true
                }
            }

            if (Config.editBlocked){
                UMessage(
                    UTextComponent("&a&l[Blocker]&r${if (shouldGray) "§7" else "§6"} SoundName: ${name}  Pitch: ${pitch}").setClick(
                        MCClickEventAction.RUN_COMMAND,
                        "/blocksound $name $pitch"
                    ).setHover(
                        MCHoverEventAction.SHOW_TEXT,
                        if (shouldGray) "§7Click to remove this sound into your block list."
                        else "§6Click to add this sound into your block list."
                    )
                ).chat()


            }


        }
    }
    fun addOrRemove(soundProperty: SoundProperty) : String{
        var result: String
        if (Config.blockSoundMode==0){
            if (blockedSoundNames.contains(soundProperty.name)) {
                blockedSoundNames.remove(soundProperty.name)
                result = "SUCCESSFULLY REMOVED!"
            } else {
                blockedSoundNames.add(soundProperty.name)
                result = "SUCCESSFULLY ADDED!"
            }
            Config.blockedSoundsNormal = GenericSetJsonUtil.toJson(blockedSoundNames)
        }else {
            if (blockedSounds.contains(soundProperty)) {
                blockedSounds.remove(soundProperty)
                result = "SUCCESSFULLY REMOVED!"
            } else {
                blockedSounds.add(soundProperty)
                result = "SUCCESSFULLY ADDED!"
            }
            Config.blockedSoundsStrict = GenericSetJsonUtil.toJson(blockedSounds)
        }
        Config.markDirty()
        Config.writeData()
        return result
    }
}

fun isPlayerInGame(): Boolean {
    val mc = Minecraft.getMinecraft()


    // 1. 确保世界对象已加载
    // 2. 确保实体玩家对象已生成
    // 3. 确保当前没有打开任何全屏系统菜单（如主菜单、加载界面），但允许打开背包或聊天栏
    return mc.theWorld != null && mc.thePlayer != null && (mc.currentScreen == null || !mc.currentScreen.doesGuiPauseGame())
}
data class SoundProperty(
    val name: String,
    val pitch: Float
)