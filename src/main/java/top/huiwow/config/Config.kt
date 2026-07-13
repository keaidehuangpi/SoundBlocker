package top.huiwow.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import top.huiwow.Main
import top.huiwow.utils.GenericSetJsonUtil
import java.io.File
import java.util.function.Consumer
import kotlin.reflect.jvm.javaField

/**
 * An example configuration which gives an overview of all property types,
 * as well as a visual demonstration of each option. Also demos some
 * aspects such as fields with different initial values.
 */
object Config : Vigilant(File("./config/soundBlocker/config.toml")) {


    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Sound Blocker",
        description = "Why would you download this and just keep it disable?",
        category = "Main",
    )
    var enabled = false


    @Property(
        type = PropertyType.SWITCH,
        name = "Edit Blocked Sounds",
        description = "Enable that and you can edit the sounds to block in chat.",
        category = "Main",
    )
    var editBlocked = false


    @Property(
        type = PropertyType.PARAGRAPH,
        name = "Blocked Sounds",
        description = "Storages the sounds you have blocked in strict mode.Don't edit this unless you know what you're doing!!!",
        category = "Main",
    )
    var blockedSoundsStrict = ""

    @Property(
        type = PropertyType.PARAGRAPH,
        name = "Blocked Sounds",
        description = "Storages the sounds you have blocked in normal mode.Don't edit this unless you know what you're doing!!!",
        category = "Main",
    )
    var blockedSoundsNormal = ""

    @Property(
        type = PropertyType.SELECTOR,
        name = "Block Sound mode",
        description = "Choose the mode you wanna use.In strict mode,the mod will check the sound name and pitch.In normal mode,the mod only checks the name.NOTE that the blocked sounds are not shared between these modes.",
        category = "Main",
        options = ["Normal", "Strict"]
    )
    var blockSoundMode = 0

    init {
        initialize()

        /*val clazz = javaClass
        registerListener(clazz.getDeclaredField("colorWithAlpha")) { color: Color ->
            UChat.chat("colorWithAlpha listener activated! New color: $color")
        }*/
        Config.registerListener<String>(::blockedSoundsStrict.javaField!!, Consumer<String> {
            Main.Instance?.soundBlocker?.let { it1 -> it1.blockedSounds = GenericSetJsonUtil.fromJson(it) }
        })
        Config.registerListener<String>(::blockedSoundsNormal.javaField!!, Consumer<String> {
            Main.Instance?.soundBlocker?.let { it1 -> it1.blockedSoundNames = GenericSetJsonUtil.fromJson(it) }
        })




        setCategoryDescription(
            "Main",
            "The Main functions"
        )
    }
}