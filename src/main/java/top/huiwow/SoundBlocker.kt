package top.huiwow

import net.minecraft.client.Minecraft
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import top.huiwow.config.Config
import top.huiwow.utils.GenericSetJsonUtil

class SoundBlocker {
    var blockedSoundNames = HashSet<String>()
    var blockedSounds = HashSet<SoundProperty>()

    @SubscribeEvent
    fun onPacket(event: PlaySoundEvent) {
        if (!Config.enabled || !isPlayerInGame()) {
            return
        }

        val sound = event.sound ?: return
        val name = event.name
        val pitch = sound.pitch
        val receivedSound = SoundProperty(name, pitch)
        var shouldGray = false

        when (Config.blockSoundMode) {
            0 -> if (blockedSoundNames.contains(receivedSound.name)) {
                event.result = null
                shouldGray = true
            }

            1 -> if (blockedSounds.contains(receivedSound)) {
                event.result = null
                event.isCanceled = true
            }
        }

        if (!Config.editBlocked) {
            return
        }

        if (shouldGray && Config.hideDisabledSounds) {
            return
        }

        sendEditableSoundMessage(name, pitch, shouldGray)
    }

    private fun sendEditableSoundMessage(name: String, pitch: Float, shouldGray: Boolean) {
        val color = if (shouldGray) EnumChatFormatting.GRAY else EnumChatFormatting.GOLD
        val hoverText = if (shouldGray) {
            "${EnumChatFormatting.GRAY}Click to remove this sound from your block list."
        } else {
            "${EnumChatFormatting.GOLD}Click to add this sound to your block list."
        }

        val message = ChatComponentText(
            "${EnumChatFormatting.GREEN}${EnumChatFormatting.BOLD}[Blocker]" +
                    "${EnumChatFormatting.RESET}$color SoundName: $name  Pitch: $pitch"
        )
        message.chatStyle = ChatStyle()
            .setChatClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/blocksound $name $pitch"))
            .setChatHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText(hoverText)))

        Minecraft.getMinecraft().thePlayer.addChatMessage(message)
    }

    fun addOrRemove(soundProperty: SoundProperty): String {
        val result: String
        if (Config.blockSoundMode == 0) {
            if (blockedSoundNames.contains(soundProperty.name)) {
                blockedSoundNames.remove(soundProperty.name)
                result = "SUCCESSFULLY REMOVED!"
            } else {
                blockedSoundNames.add(soundProperty.name)
                result = "SUCCESSFULLY ADDED!"
            }
            Config.blockedSoundsNormal = GenericSetJsonUtil.toJson(blockedSoundNames)
        } else {
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
    return mc.theWorld != null && mc.thePlayer != null && (mc.currentScreen == null || !mc.currentScreen.doesGuiPauseGame())
}

data class SoundProperty(
    val name: String,
    val pitch: Float
)
