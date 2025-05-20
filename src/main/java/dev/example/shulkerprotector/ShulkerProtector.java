package dev.example.shulkerprotector;

import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDespawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.NukkitRunnable;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShulkerProtector extends PluginBase implements Listener {

    private final Set<UUID> protectedShulkers = ConcurrentHashMap.newKeySet();

    @Override
    public void onEnable() {
        getLogger().info("ShulkerProtector enabled");
        getServer().getPluginManager().registerEvents(this, this);

        new NukkitRunnable() {
            @Override
            public void run() {
                getServer().getLevels().values().forEach(level -> {
                    for (EntityItem item : level.getEntitiesByClass(EntityItem.class)) {
                        if (isShulkerBox(item.getItem())) {
                            protectedShulkers.add(item.getUniqueId());
                        }
                    }
                });
            }
        }.runTaskTimer(this, 20, 20);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof EntityItem) {
            EntityItem item = (EntityItem) event.getEntity();
            if (protectedShulkers.contains(item.getUniqueId())) {
                event.setCancelled();
                item.close(); // удаляем без дропа
            }
        }
    }

    @EventHandler
    public void onEntityDespawn(EntityDespawnEvent event) {
        if (event.getEntity() instanceof EntityItem) {
            protectedShulkers.remove(event.getEntity().getUniqueId());
        }
    }

    private boolean isShulkerBox(Item item) {
        int id = item.getId();
        return id >= 219 && id <= 234; // все варианты окрашенных шалкеров
    }
}
