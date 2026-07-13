package top.huiwow

import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import top.huiwow.commands.BlockSoundCommand
import top.huiwow.commands.SoundBlockerCommand
import top.huiwow.config.Config

@Mod(modid = Main.Companion.ID, name = Main.Companion.NAME, version = Main.Companion.VER)
class Main {
    val soundBlocker = SoundBlocker()
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent?) {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent?) {
        Config.initialize()
        val soundBlockerCommand = SoundBlockerCommand()
        ClientCommandHandler.instance.registerCommand(soundBlockerCommand)
        ClientCommandHandler.instance.registerCommand(BlockSoundCommand())
        MinecraftForge.EVENT_BUS.register(soundBlockerCommand)
        MinecraftForge.EVENT_BUS.register(soundBlocker)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent?) {
    }





    companion object {
        const val NAME: String = "SoundBlocker"
        const val VER: String = "1.0.0"
        const val ID: String = "soundblocker"

        @Mod.Instance(ID)
        var Instance: Main? = null
    }
}
