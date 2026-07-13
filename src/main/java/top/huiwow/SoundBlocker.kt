package top.huiwow

import gg.essential.universal.utils.MCClickEventAction
import gg.essential.universal.utils.MCHoverEventAction
import gg.essential.universal.wrappers.message.UMessage
import gg.essential.universal.wrappers.message.UTextComponent
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



        val sound = event.sound
        if (sound != null) {
            val name =event.name
            val pitch = sound.pitch

            val receivedSound = SoundProperty(name,pitch)
            var shouldGray : Boolean = false

            when(Config.blockSoundMode){
                0-> if (blockedSoundNames.contains(receivedSound.name)){
                    event.isCanceled= true
                    shouldGray=true
                }
                1-> if (blockedSounds.contains(receivedSound)){
                    shouldGray=true
                    event.isCanceled= true
                }
            }

            if (Config.editBlocked){
                UMessage(
                    UTextComponent("&a&l[SoundBlocker]&r${if (shouldGray) "§7" else "§6"} SoundName: ${name}  Pitch: ${pitch}").setClick(
                        MCClickEventAction.RUN_COMMAND,
                        "/blocksound $name $pitch"
                    ).setHover(
                        MCHoverEventAction.SHOW_TEXT,
                        if (shouldGray) "§7Click to remove this sound into your block list."
                        else "§6Click to add this sound into your block list."

                    ).chat()
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
        return result
    }
}
data class SoundProperty(
    val name: String,
    val pitch: Float
)