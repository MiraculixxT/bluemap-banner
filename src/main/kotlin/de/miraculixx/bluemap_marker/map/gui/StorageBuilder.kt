package de.miraculixx.bluemap_marker.map.gui

import de.miraculixx.bluemap_marker.utils.messages.cHighlight
import de.miraculixx.bluemap_marker.utils.messages.cmp
import de.miraculixx.bluemap_marker.utils.messages.emptyComponent
import de.miraculixx.bluemap_marker.utils.messages.plus
import net.axay.kspigot.items.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Inline Builder for GUI type - Storages
 * Use storage GUIs to display a lot of content with a minimal of placeholders. They can be filtered, scrollable and supports menus
 * @author Miraculixx
 */
inline fun storageBuilder(builder: StorageGUI.InlineBuilder.() -> Unit) = StorageGUI.InlineBuilder().apply(builder).build()

class StorageGUI(
    private val content: Map<ItemStack, Boolean>,
    private val header: List<ItemStack>,
    private val filterable: Boolean,
    private val filterName: String?,
    private val scrollable: Boolean,
    private val player: Player?,
    title: Component
) {
    private val inventory = Bukkit.createInventory(player, 6 * 9, title)

    private constructor(builder: InlineBuilder) : this(
        buildMap {
            putAll(builder.markableItems)
            builder.items.forEach { put(it, false) }
        },
        buildList {
            addAll(builder.headers)
            builder.header?.let { add(it) }
        },
        builder.filterable,
        builder.filterName,
        builder.scrollable,
        builder.player,
        builder.title
    )

    class InlineBuilder {
        /**
         * Import items that are markable. Marked items will be displayed with either an enchanting glint or and be replaced with a shiny green glass pane, if they not support enchanting glints.
         * @see items
         */
        var markableItems: Map<ItemStack, Boolean> = emptyMap()

        /**
         * Import items to the storage GUI.
         * @see markableItems
         */
        var items: List<ItemStack> = emptyList()

        /**
         * Decorate the storage header (first row) with custom items. You can set
         * - 0 Items for no header
         * - 1 Item ----o----
         * - 2 Items ---o-o---
         * - 3 Items --o--o--o--
         * - 4 Items -o-o-o-o-
         * @see header
         */
        var headers: List<ItemStack> = emptyList()

        /**
         * Decorate the storage header (first row) with a custom item. It will be centered
         * @see headers
         */
        var header: ItemStack? = null

        /**
         * Configure the GUI as filterable. Filterable storage GUIs will have a placeholder row at the bottom with a centered filter switcher
         *
         * **Default: false**
         * @see filterName
         */
        var filterable: Boolean = false

        /**
         * Override the GUI filter. If null is provided, the GUI will try to use the last used filter. By default, it is "No Filter".
         *
         * **ONLY works if GUI is filterable!**
         * @see filterName
         */
        var filterName: String? = null

        /**
         * Configure the GUI as scrollable. Scrollable storage GUIs will have up and down arrows to navigate on overflow. On disabled scroll GUIs, overflow will be ignored.
         *
         * **Default: false**
         */
        var scrollable: Boolean = false

        /**
         * Connect a player to this GUI instance. This will allow [filterName] to find the previous settings and let to run further operations without player context.
         */
        var player: Player? = null

        /**
         * Sets the inventory title for this storage GUI.
         */
        var title: Component = emptyComponent()

        /**
         * Internal use. No need to call it inlined
         */
        fun build() = StorageGUI(this)
    }

    private fun build() {
        //Filter Calculation
        val finalFilter = if (filterName == null && filterable) {
            val preInv = player?.openInventory?.topInventory
            if (preInv?.size != 6 * 9) null
            else {
                val meta = preInv.getItem(49)?.itemMeta
                meta?.persistentDataContainer?.get(NamespacedKey("miraculixx-api", "gui.storage.filter"), PersistentDataType.STRING)
            }
        } else filterName

        //Header
        when (header.size) {
            1 -> inventory.setItem(4, header[0])
            2 -> {
                inventory.setItem(3, header[0])
                inventory.setItem(5, header[1])
            }

            3 -> {
                inventory.setItem(2, header[0])
                inventory.setItem(4, header[1])
                inventory.setItem(6, header[2])
            }

            4 -> {
                inventory.setItem(1, header[0])
                inventory.setItem(3, header[1])
                inventory.setItem(5, header[2])
                inventory.setItem(7, header[2])
            }
        }

        //Filter Apply
        if (filterName != null) {
            inventory.setItem(49, itemStack(Material.HOPPER) {
                meta {
                    customModel = 205
                    name = cmp("Filters", cHighlight, bold = true)
                    lore(
                        listOf(
                            emptyComponent(),
                            cmp("Filter", cHighlight, underlined = true),
                            cmp("∙ $finalFilter"),
                            emptyComponent(),
                            cmp("Click ", cHighlight) + cmp("≫ Change Filter")
                            )
                    )
                    persistentDataContainer.set(NamespacedKey("miraculixx-api","gui.storage.filter"), PersistentDataType.STRING, finalFilter ?: "No Filter")
                }
            })
        }

        //Content
        var counter = 0
        content.forEach { (item, activated) ->
            if (activated) {
                when (item.type) {
                    Material.PLAYER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL, Material.CHEST,
                    Material.ENDER_CHEST, Material.TRAPPED_CHEST -> item.type = Material.LIME_STAINED_GLASS_PANE
                    else -> {}
                }

                val meta = item.itemMeta
                meta.addEnchant(Enchantment.MENDING, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                item.itemMeta = meta
            }
            if (((scrollable || filterable) && counter >= 24) || counter >= 30) return
            inventory.setItem(9 + counter, item)
            counter++
        }
    }

    private fun fillPlaceholder() {
        val darkHolder = itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { name = emptyComponent() } }
        val lightHolder = itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) { meta { name = emptyComponent() } }
        (0..8).forEach { inventory.setItem(it, darkHolder) }
        (9..53).forEach { inventory.setItem(it, lightHolder) }
        if (scrollable || filterable) (44..53).forEach { inventory.setItem(it, darkHolder) }
    }

    /**
     * Get the final inventory object for further operations.
     * @return Crafted storage GUI
     */
    fun get(): Inventory {
        return inventory
    }

    /**
     * Open the final inventory for a player.
     * @param player Target Player - Not needed if already set in builder
     */
    fun open(player: Player? = null) {
        player?.openInventory(inventory)
        this.player?.openInventory(inventory)
    }

    init {
        fillPlaceholder()
        build()
    }
}

