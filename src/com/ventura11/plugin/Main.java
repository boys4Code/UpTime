package com.ventura11.plugin;

import net.minecraft.server.v1_12_R1.PlayerSelector;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    private FileConfiguration ymlConfig = null;
    private File ymlFile = null;
    public List<UUID> players = new ArrayList<UUID>();
    public int db;

    @Override
    public void onEnable() {
        System.out.println("A plugin elindúlt");
        Bukkit.getPluginManager().registerEvents(this, this);

        this.getConfig().options().copyDefaults();
        saveDefaultConfig();
        db = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                timer();
            }
        }, 0L, 20L);
    }

    @Override
    public void onLoad() {
        List<Player> asd = new ArrayList<Player>();
        asd.addAll(Bukkit.getOnlinePlayers());
        for(int i = 0; i < asd.toArray().length; i++) {
            UUID u = asd.get(i).getUniqueId();
            players.add(u);
        }
    }

    @Override
    public void onDisable() {
        saveYmlConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Calendar c = Calendar.getInstance();
        getYmlConfig().set("uptime." + e.getPlayer().getUniqueId().toString() + ".Join", c.getTime().toString());
        saveYmlConfig();
        reloadYmlCOnfig();
        if(!(getYmlConfig().isSet("uptime." + e.getPlayer().getUniqueId().toString() + ".Sec"))){
            getYmlConfig().set("uptime." + e.getPlayer().getUniqueId().toString() + ".Sec", 0);
        }
        players.add(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        Calendar c = Calendar.getInstance();
        getYmlConfig().set("uptime." + e.getPlayer().getUniqueId().toString() + ".Leave", c.getTime().toString());
        saveYmlConfig();
        reloadYmlCOnfig();
        players.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

        if(cmd.getName().equals("uptime")){
            if(args.length < 1){
                player.sendMessage("/uptime <player|del>");
            }else{
                if (args[0].equalsIgnoreCase("del")) {
                    player.sendMessage("Sikeresen törölted mindenki uptime-ját.");
                    getYmlConfig().set("uptime..", null);
                }else{
                    Player p = this.getServer().getPlayer(args[0]);
                    if (Bukkit.getPlayerExact(args[0]) == null){
                        player.sendMessage("Ez a játékos még nem lépett fel a szerverre!");
                    }else {
                        int time = getYmlConfig().getInt("uptime." + p.getUniqueId().toString() + ".Sec");
                        player.sendMessage("" + time);
                    }
                }
            }
        }
        return false;
    }

    public void reloadYmlCOnfig(){
        if(ymlFile == null){
            ymlFile = new File(getDataFolder(), "playerdata.yml");
        }
        ymlConfig = YamlConfiguration.loadConfiguration(ymlFile);
    }

    public FileConfiguration getYmlConfig(){
        if(ymlConfig == null){
            reloadYmlCOnfig();
        }
        return ymlConfig;
    }

    public void saveYmlConfig(){
        if(ymlConfig == null || ymlFile == null){
            return;
        }
        try{
            getYmlConfig().save(ymlFile);
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
    public void timer(){
        for (int i = 0; i < players.toArray().length; i++){
            UUID u = players.get(i);
            int temp = getYmlConfig().getInt("uptime." + u.toString() + ".Sec");
            temp +=1;
            getYmlConfig().set("uptime." + u.toString() + ".Sec", temp);
            saveYmlConfig();
        }
    }
}
