package me.notsodelayed.thenexus.kit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.notsodelayed.simmygameapi.api.registry.Registry;
import me.notsodelayed.simmygameapi.util.FileUtil;
import me.notsodelayed.simmygameapi.util.MessageUtil;
import me.notsodelayed.simmygameapi.util.Util;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.simmygameapi.api.exception.InvalidYamlException;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO nuke this shit in future
/**
 * Manages {@link NexusKit} registrations.
 * @see Registry
 */
public class NexusKitManager {

    public enum LoadState {

        IDLE("idle"),
        INIT("initialize"),
        RELOADING("reloading");

        private final String toString;

        LoadState(String toString) {
            this.toString = toString;
        }

        @Override
        public String toString() {
            return toString;
        }

    }

    private static NexusKitManager kitManager;
    public static final String[] validKitTypes = new String[] {
            "classic",
            "potion",
            "trigger-potion"
    };
    public static final String[] validTriggerActionTypes = new String[] {
            "sneak"
    };
    private final Map<String, NexusKit> KITS = new HashMap<>();
    @ApiStatus.Internal
    private LoadState loadState;

    private NexusKitManager(TheNexus ignored) {}

    public static NexusKitManager get() {
        if (kitManager == null) {
            TheNexus plugin = TheNexus.instance;
            kitManager = new NexusKitManager(plugin);

            kitManager.loadState = LoadState.INIT;
            TheNexus.logger.info("Registering kits...");
            File kitsDirectory = new File(plugin.getDataFolder(), "kits");
            if (!kitsDirectory.exists()) {
                try {
                    ZipFile jar = new ZipFile(TheNexus.pluginFile);
                    TheNexus.logger.info("Generating default-embedded kits...");
                    Iterator<ZipEntry> iterator = (Iterator<ZipEntry>) jar.stream().iterator();
                    while (iterator.hasNext()) {
                        ZipEntry entry = iterator.next();
                        if (entry.isDirectory())
                            continue;
                        if (entry.getName().startsWith("kits/") && entry.getName().endsWith(".yml")) {
                            FileUtil.saveFromInputStream(jar.getInputStream(entry), new File(plugin.getDataFolder(), "kits" + File.separator + (entry.getName().split("/")[1])));
                        }
                    }
                    jar.close();
                } catch (IOException ex) {
                    TheNexus.logger.warning("Unable to generate default kits! Please manually extract 'kits' folder from the jar into " + kitsDirectory.getAbsolutePath());
                    ex.printStackTrace(System.err);
                }
            }
            if (kitsDirectory.listFiles() != null) {
                for (File ymlFile : kitsDirectory.listFiles()) {
                    if (ymlFile.getName().startsWith("-"))
                        continue;
                    NexusKit kit;
                    try {
                        kit = kitManager.registerKit(ymlFile);
                    } catch (IllegalArgumentException ex) {
                        // Simply suppress exception for none YML files
                        continue;
                    } catch (InvalidYamlException ex) {
                        TheNexus.logger.warning("Skipping kit registration from " + ymlFile);
                        TheNexus.logger.warning(ex.getMessage());
                        continue;
                    }
                    TheNexus.logger.info("Registered kit: " + kit.getOptionalDisplayName().orElse(kit.getId()));
                }
                TheNexus.logger.info("Successfully registered " + kitManager.getKits().size() + " kits!");
            }
            kitManager.loadState = LoadState.IDLE;
        }
        return kitManager;
    }

    /**
     * @param requester the requester
     * @return whether the request is approved and proceeds
     */
    public boolean reload(CommandSender requester) {
        if (loadState != LoadState.IDLE)
            return false;
        CompletableFuture.runAsync(() -> Bukkit.getScheduler().runTask(TheNexus.instance, () -> {
            loadState = LoadState.RELOADING;
            kitManager = null;
            NexusKitManager.get();
            loadState = LoadState.IDLE;
            MessageUtil.sendSuccessMessage(requester, "Kits reload complete!", "Successfully registered " + kitManager.getKits().size() + " kits!");
        }));
        return true;
    }

    /**
     * @param id the id
     * @param displayName the display name
     * @param displayItem the display item
     * @param description the description
     * @return the created kit
     */
    public NexusKit registerClassicKit(@NotNull String id, @Nullable String displayName, @NotNull Material displayItem, @Nullable String[] description) {
        NexusKit kit = new NexusKit(id, displayName, displayItem, description);
        KITS.put(id, kit);
        return kit;
    }

    /**
     * @param id the id
     * @param displayName the display name
     * @param displayItem the display item
     * @param description the description
     * @param potionEffects the potion effects
     * @return the created kit
     */
    public PotionNexusKit registerPotionKit(@NotNull String id, @Nullable String displayName, @NotNull Material displayItem, @Nullable String[] description, @NotNull PotionEffect[] potionEffects) {
        PotionNexusKit kit = new PotionNexusKit(id, displayName, displayItem, description, potionEffects);
        KITS.put(id, kit);
        return kit;
    }

    /**
     * @param id the id
     * @param displayName the display name
     * @param displayItem the display item
     * @param description the description
     * @param potionEffects the potion effects
     * @param triggerAction the trigger action
     * @return the created kit
     */
    public TriggerPotionNexusKit registerTriggerPotionKit(@NotNull String id, @Nullable String displayName, @NotNull Material displayItem, @Nullable String[] description, @NotNull PotionEffect[] potionEffects, @NotNull TriggerAction triggerAction) {
        TriggerPotionNexusKit kit = new TriggerPotionNexusKit(id, displayName, displayItem, description, potionEffects, triggerAction);
        KITS.put(id, kit);
        return kit;
    }

    private static final Pattern PATTERN_ITEM_FORMAT = Pattern.compile("\\d(:.+)");

    /**
     * Registers a kit from a {@link YamlConfiguration}.
     * @param ymlFile the yml file
     * @return the registered kit
     * @throws IllegalArgumentException if ymlFile is not a YML file, or is a disabled kit YML
     * @throws InvalidYamlException if ymlFile is not a valid kit yml, or a half-baked kit yml
     */
    @NotNull
    public NexusKit registerKit(@NotNull File ymlFile) throws IllegalArgumentException, InvalidYamlException {
        FileUtil.checkFileExtensionOrThrow(ymlFile, ".yml");
        if (ymlFile.getName().startsWith("-"))
            throw new IllegalArgumentException(ymlFile.getName() + " is marked as a disabled kit YML");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(ymlFile);
        String kitId = ymlFile.getName().split(".yml")[0];

        // kit-type
        // Pre-check whether is valid, before we start caching the rest
        String kitType = yml.getString("kit-type", "null");
        if (!StringUtils.equalsAny(kitType, validKitTypes))
            throw new InvalidYamlException(kitId, "kit-type", StringUtils.joinWith(", ", (Object[]) validKitTypes), kitType);

        // If kit-type = potion/trigger-potion, pre-check whether 'potion' is valid
        PotionEffect potionEffect = null;
        TriggerAction triggerAction = null;
        if (kitType.equals("potion") || kitType.equals("trigger-potion")) {

            // kit-type potion: register PotionEffectType
            String[] potionArgument = new String[] {yml.getString("potion")};
            if (potionArgument[0] == null)
                throw new InvalidYamlException(kitId, "potion", "any PotionEffectType enum", "null");

            potionArgument = potionArgument[0].split(":", 2);
            PotionEffectType type = org.bukkit.Registry.EFFECT.get(new NamespacedKey("minecraft", potionArgument[0]));
            if (type == null)
                throw new InvalidYamlException(kitId, "potion", "any PotionEffectType enum", potionArgument[0]);
            potionEffect = type.createEffect(PotionEffect.INFINITE_DURATION, Util.parseIntOrDefault(potionArgument[1], 1) - 1);

            // kit-type trigger-potion: register TriggerAction
            if (kitType.equals("trigger-potion")) {
                try {
                    triggerAction = TriggerAction.valueOf(yml.getString("trigger-action").toUpperCase(Locale.ENGLISH));
                } catch (IllegalArgumentException ex) {
                    throw new InvalidYamlException(kitId, "trigger-action", StringUtils.joinWith(", ", (Object[]) validTriggerActionTypes), yml.getString("trigger-action", "null"));
                }
            }
        }

        // Parse kit configuration
        String displayName = yml.getString("display-name");
        Material displayItem = Material.CHEST;
        try {
            displayItem = Material.valueOf(yml.getString("display-item", "null").toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException ex) {
            TheNexus.logger.warning(ymlFile.getName() + " provided an invalid 'display-item' (" + yml.getString("display-item") + "). Defaulting to " + displayItem + "...");
        }
        String[] description = yml.getStringList("description").toArray(new String[0]);
        boolean soulboundDefault = yml.getBoolean("soulbound-default", false);
        boolean unbreakableDefault = yml.getBoolean("unbreakable-default", false);
        List<ItemStack> items = new ArrayList<>();
        short index = -1;
        for (String itemValue : yml.getStringList("items")) {
            index++;
            // TODO temp bypass
//            if (PATTERN_ITEM_FORMAT.matcher(itemValue).find()) {
//                TheNexus.logger.warning("Skipping " + ymlFile.getName() + ":items:element" + (index + 1) + " due to invalid item format...");
//                TheNexus.logger.info("Value: " + itemValue);
//                continue;
//            }
            ItemStack finalItemStack;
            List<String> itemValueSplit = Arrays.stream(itemValue.split(":", 3)).toList();
            boolean hasNbt = itemValueSplit.size() == 3;
            try {
                int amount = Integer.parseInt(itemValueSplit.get(0));
                Material material = Material.valueOf(itemValueSplit.get(1).toUpperCase(Locale.ENGLISH));
                finalItemStack = new ItemStack(material, amount);
                if (hasNbt) {
                    NBTContainer itemNBT = NBTItem.convertItemtoNBT(finalItemStack);
                    itemNBT.getOrCreateCompound("tag").mergeCompound(new NBTContainer(itemValueSplit.get(2)));
                    NBTCompound optionalNBTs = new NBTContainer();
                    if (soulboundDefault)
                        optionalNBTs.mergeCompound(new NBTContainer("{Soulbound:1b}"));
                    if (unbreakableDefault)
                        optionalNBTs.mergeCompound(new NBTContainer("{Unbreakable:1b}"));
                    if (!optionalNBTs.getKeys().isEmpty())
                        itemNBT.mergeCompound(optionalNBTs);
                    finalItemStack = NBTItem.convertNBTtoItem(itemNBT);
                    if (finalItemStack == null) {
                        TheNexus.logger.warning("[" + ymlFile.getName() + "] Skipping " + ymlFile.getName() + ":items:element" + (index + 1) + " due to invalid json NBT...");
                        continue;
                    }
                }
                items.add(index, finalItemStack);
            } catch (Exception ex) {
                TheNexus.logger.warning("[" + ymlFile.getName() + "] Skipping items:element" + (index + 1) + " due to invalid item format: " + ex.getClass() + ": " + ex.getMessage());
            }
        }
        NexusKit kit = switch (kitType) {
            case "classic" -> kitManager.registerClassicKit(kitId, displayName, displayItem, description);
            case "potion" -> kitManager.registerPotionKit(kitId, displayName, displayItem, description, new PotionEffect[]{potionEffect});
            case "trigger-potion" -> kitManager.registerTriggerPotionKit(kitId, displayName, displayItem, description, new PotionEffect[]{potionEffect}, triggerAction);
            // It will NEVER reach here AT ALL, and must be looked into it if otherwise!
            default -> throw new RuntimeException("Unexpected end of switch while handling filtered kit-type value '" + kitType + "'. Contact the plugin developer immediately!");
        };
        kit.setItems(items);
        KITS.put(kitId, kit);
        return kit;
    }

    /**
     * @return an immutable registered kits, mapped by their respective IDs
     */
    public Map<String, NexusKit> getKits() {
        return Map.copyOf(KITS);
    }

}
