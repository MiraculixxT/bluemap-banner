package de.miraculixx.bluemap_marker.commands.marker

import com.flowpowered.math.vector.Vector3d
import com.mojang.brigadier.arguments.StringArgumentType
import de.miraculixx.bluemap_marker.utils.messages.*
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.*
import net.axay.kspigot.extensions.broadcast
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.coordinates.Coordinates
import net.minecraft.commands.arguments.coordinates.Vec3Argument

class MarkerCommand {
    private val builder: MutableMap<String, MarkerBuilder> = mutableMapOf()

    val mainCommand = command("bmarker") {
        literal("help") {
            runs {

            }
        }
        literal("create") {
            argument<String>("type", StringArgumentType.word()) {
                suggestList { listOf("poi", "html", "line", "shape", "extrude") }
                runs {
                    if (builder.contains(sender.textName)) {
                        sender.bukkitSender.sendMessage(prefix + cmp("You already started a marker setup! ", cError) + literalText {
                            component(cmp("Cancel", cError, underlined = true))
                            clickEvent = ClickEvent.runCommand("/bmarker cancel")
                            hoverEvent = HoverEvent.showText(cmp("/bmarker cancel"))
                        } + cmp(" or ", cError) + literalText {
                            component(cmp("build", cError, underlined = true))
                            clickEvent = ClickEvent.runCommand("/bmarker build")
                            hoverEvent = HoverEvent.showText(cmp("/bmarker build"))
                        } + cmp(" it before creating a new one", cError))
                    } else {
                        val type = getArgument<String>("type")
                        val supported = when (type) {
                            "poi" -> "position, label, anchor, min-distance, max-distance"
                            else -> "unknown"
                        }

                        val markerType = enumOf<MarkerType>(type.uppercase())
                        if (markerType == null) {
                            sender.bukkitSender.sendMessage(prefix + cmp("This is not a valid marker!", cError))
                            return@runs
                        }
                        builder[sender.textName] = MarkerBuilder(markerType)
                        broadcast(sender.textName)
                        sender.bukkitSender.sendMessage(prefix + cmp("Marker setup started! Modify values using ") + literalText {
                            component(cmp("/bmarker-setup", cMark, underlined = true))
                            clickEvent = ClickEvent.suggestCommand("/bmarker-setup ")
                            hoverEvent = HoverEvent.showText(cmp("Use /bmarker-setup <arg> <value>"))
                        } + cmp(" and finish your setup with ") + literalText {
                            component(cmp("/bmarker build", cMark, underlined = true))
                            clickEvent = ClickEvent.runCommand("/bmarker build")
                            hoverEvent = HoverEvent.showText(cmp("/bmarker build"))
                        })
                        sender.bukkitSender.sendMessage(prefix + cmp("Supported values: ") + cmp(supported, cMark))
                    }
                }
            }
        }
        literal("build") {
            runs {
                if (!builder.contains(sender.textName)) noMarkerBuilder(sender)
                else {
                    val marker = builder[sender.textName]?.buildMarker()
                    sender.bukkitSender.sendMessage(prefix + cmp("Marker created! It should appear on your BlueMap in a few seconds", cSuccess))
                }
            }
        }
        literal("delete") {
            runs {
                //TODO
            }
        }
        literal("cancel") {
            runs {
                if (builder.remove(sender.textName) == null) noMarkerBuilder(sender)
                else sender.bukkitSender.sendMessage(prefix + cmp("Canceled current marker setup!"))
            }
        }

    }

    val enterCommand = command("bmarker-setup") {
        literal("label") {
            argument<String>("label", StringArgumentType.greedyString()) {
                runs {
                    val label = getArgument<String>("label")
                    builder.getOrElse(sender.textName) { noMarkerBuilder(sender); return@runs }.pLabel = label
                    sender.bukkitSender.sendMessage(prefix + cmp("Label $label applied", cSuccess))
                }
            }
        }
        literal("position") {
            argument<Coordinates>("position", Vec3Argument(true)) {
                runs {
                    val position = getArgument<Coordinates>("position").getPosition(sender)
                    val vec3d = Vector3d(position.x, position.y, position.z)
                    builder.getOrElse(sender.textName) { noMarkerBuilder(sender); return@runs }.pPosition = vec3d
                    sender.bukkitSender.sendMessage(prefix + cmp("Position $vec3d applied", cSuccess))
                }
            }
        }
    }

    private fun noMarkerBuilder(sender: CommandSourceStack) {
        sender.bukkitSender.sendMessage(
            prefix + cmp("You have no current marker setups. Start one with ", cError) + literalText {
                component(cmp("/bmarker create", cError, underlined = true))
                clickEvent = ClickEvent.suggestCommand("/bmarker create ")
                hoverEvent = HoverEvent.showText(cmp("Start a marker setup (click)"))
            }
        )
    }
}