package top.huiwow.commands

import gg.essential.universal.UScreen
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import top.huiwow.config.Config

class BlockSoundCommand : CommandBase() {
    private var openConfigNextTick = false

    // 命令的名称，触发用：/mymod
    override fun getCommandName(): String = "blocksound"

    // 命令的别名，触发用：/mm
    override fun getCommandAliases(): List<String> = listOf("blocksounds")

    // 命令用法提示
    override fun getCommandUsage(sender: ICommandSender): String = "/blocksound"

    // 权限等级，0 代表任何人都能用（客户端命令必须为 0）
    override fun getRequiredPermissionLevel(): Int = 0

    // 纯客户端命令强烈建议重写此方法并返回 true，确保任何玩家都能执行
    override fun canCommandSenderUseCommand(sender: ICommandSender): Boolean = true

    // 核心执行逻辑
    override fun processCommand(sender: ICommandSender, args: Array<out String>) {
        /*if (args.size == 2) {
            SoundBlocker.blockedSounds.add(SoundProperty(args[0], parseDouble(args[1]).toFloat()))
            UChat.chat("Successfully added!")
        } else if (args.size == 1) {
            SoundBlocker.blockedALL.add(args[0])
            UChat.chat("Successfully added a type of blocked sounds!")
        } else {
            UChat.chat("Wrong usage!")
        }*/

        //window.renderScreenUntilClosed()
       // gg.essential.universal.render.
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END || !openConfigNextTick) {
            return
        }

        openConfigNextTick = false
        UScreen.displayScreen(Config.gui())
    }
}
