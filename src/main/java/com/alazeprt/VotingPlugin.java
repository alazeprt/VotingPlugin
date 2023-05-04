package com.alazeprt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class VotingPlugin extends JavaPlugin implements CommandExecutor, TabCompleter {
    @Override
    public void onEnable() {
        File votelist = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "votelist.yml");
        File playerlist = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "playerlist.yml");
        if(!votelist.exists()) {
            saveResource("votelist.yml", false);
        }
        if(!playerlist.exists()) {
            saveResource("playerlist.yml", false);
        }
        Objects.requireNonNull(getCommand("vote")).setExecutor(this);
        Objects.requireNonNull(getCommand("vote")).setTabCompleter(this);
        getLogger().log(Level.INFO, "§aVotingPlugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "§cVotingPlugin Disabled!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            if(sender.hasPermission("vote.help")) {
                sender.sendMessage("§6§lVotingPlugin 帮助文档");
                sender.sendMessage("§a/vote & /vote help  §7- 查看VotingPlugin 帮助文档");
                if(sender.hasPermission("vote.vote")) {
                    sender.sendMessage("§a/vote vote <投票项>  §7- 向投票项投票");
                } if(sender.hasPermission("vote.initiate")) {
                    sender.sendMessage("§a/vote initiate <投票名称>  §7- 发起名为<投票名称>的投票项");
                } if(sender.hasPermission("vote.delete")) {
                    sender.sendMessage("§a/vote delete <投票名称>/<索引>  §7- 删除名为<投票名称>或指定<索引>的投票项");
                } if(sender.hasPermission("vote.list")) {
                    sender.sendMessage("§a/vote list  §7- 查看每一个投票项的名称及索引");
                } if(sender.hasPermission("vote.cancel")) {
                    sender.sendMessage("§a/vote cancel  §7- 取消你当前的投票选");
                } if(sender.hasPermission("vote.get")) {
                    sender.sendMessage("§a/vote get  §7- 获取你当前的投票项");
                }
            } else {
                sender.sendMessage("§c你没有权限执行此指令!");
            }
        } else if(args.length == 1) {
            if (args[0].equals("help")) {
                if (sender.hasPermission("vote.help")) {
                    sender.sendMessage("§6§lVotingPlugin 帮助文档");
                    sender.sendMessage("§a/vote & /vote help  §7- 查看VotingPlugin 帮助文档");
                    if (sender.hasPermission("vote.vote")) {
                        sender.sendMessage("§a/vote vote <投票项>  §7- 向投票项投票");
                    }
                    if (sender.hasPermission("vote.initiate")) {
                        sender.sendMessage("§a/vote initiate <投票名称>  §7- 发起名为<投票名称>的投票项");
                    }
                    if (sender.hasPermission("vote.delete")) {
                        sender.sendMessage("§a/vote delete <投票名称>/<索引>  §7- 删除名为<投票名称>或指定<索引>的投票项");
                    }
                    if (sender.hasPermission("vote.list")) {
                        sender.sendMessage("§a/vote list  §7- 查看每一个投票项的名称及索引");
                    }
                    if (sender.hasPermission("vote.cancel")) {
                        sender.sendMessage("§a/vote cancel  §7- 取消你当前的投票选");
                    }
                    if (sender.hasPermission("vote.get")) {
                        sender.sendMessage("§a/vote get  §7- 获取你当前的投票项");
                    }
                } else {
                    sender.sendMessage("§c你没有权限执行此指令!");
                }
            } else if (args[0].equals("list")) {
                if(sender.hasPermission("vote.list")) {
                    File votelistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "votelist.yml");
                    FileConfiguration votelist = YamlConfiguration.loadConfiguration(votelistfile);
                    List<String> votel = votelist.getStringList("votelist");
                    if (votel.size() == 0) {
                        sender.sendMessage("§c没有寻找到任何投票项!");
                    } else {
                        sender.sendMessage("§a§l投票项(" + votel.size() + "):");
                        for (String string : votel) {
                            String name = string.split(";")[0];
                            String index = string.split(";")[1];
                            String num = string.split(";")[2];
                            sender.sendMessage("§6[" + index + "] §a" + name + ", §c票数为: " + num);
                        }
                    }
                } else {
                    sender.sendMessage("§c你没有权限执行此指令!");
                }
            } else if (args[0].equals("get")) {
                if(sender.hasPermission("vote.get")) {
                    File playerlistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "playerlist.yml");
                    FileConfiguration playerlist = YamlConfiguration.loadConfiguration(playerlistfile);
                    String index = playerlist.getString("playerlist." + sender.getName() + ".vote");
                    if (index == null) {
                        sender.sendMessage("§c你貌似还没有投票...请输入/vote vote <投票项目> 以投票");
                        return false;
                    } else if (index.equals("-1")) {
                        sender.sendMessage("§c你貌似取消了你的投票...请输入/vote vote <投票项目> 以重新投票");
                        return false;
                    }
                    File votelistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "votelist.yml");
                    FileConfiguration votelist = YamlConfiguration.loadConfiguration(votelistfile);
                    List<String> votel = votelist.getStringList("votelist");
                    if (votel.size() == 0) {
                        sender.sendMessage("§c你选择的投票不存在(可能已实现或被移除)...");
                    } else {
                        for (String string : votel) {
                            String name = string.split(";")[0];
                            String index_ = string.split(";")[1];
                            if (index.equals(index_)) {
                                sender.sendMessage("§a你的投票项为: " + "§6[" + index + "] §a" + name);
                                return true;
                            }
                        }
                        sender.sendMessage("§c你选择的投票不存在(可能已实现或被移除)...");
                    }
                } else {
                    sender.sendMessage("§c你没有权限执行此指令!");
                }
            } else if (args[0].equals("cancel")) {
                if(sender.hasPermission("vote.cancel")) {
                    File playerlistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "playerlist.yml");
                    FileConfiguration playerlist = YamlConfiguration.loadConfiguration(playerlistfile);
                    String index = playerlist.getString("playerlist." + sender.getName() + ".vote");
                    if (index == null) {
                        sender.sendMessage("§c你貌似还没有投票...请输入/vote vote <投票项目> 以投票");
                        return false;
                    }
                    File votelistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "votelist.yml");
                    FileConfiguration votelist = YamlConfiguration.loadConfiguration(votelistfile);
                    List<String> votel = votelist.getStringList("votelist");
                    if (votel.size() == 0) {
                        sender.sendMessage("§c你选择的投票不存在(可能已实现或被移除)...");
                    } else {
                        int findex = 0;
                        for (String string : votel) {
                            String index_ = string.split(";")[1];
                            if (index.equals(index_)) {
                                // PlayerList
                                playerlist.set("playerlist." + sender.getName() + ".vote", -1);
                                try {
                                    playerlist.save(playerlistfile);
                                } catch (IOException e) {
                                    sender.sendMessage("§c在保存投票信息时出现了一个错误: \n" + e.getMessage() + "\n§c请联系服务器管理员解决!");
                                    return false;
                                }
                                // VoteList
                                String[] strlist = string.split(";");
                                strlist[2] = String.valueOf(Integer.parseInt(string.split(";")[2])-1);
                                String newscore = "";
                                for(int i=0;i<strlist.length;i++) {
                                    newscore += strlist[i];
                                    if(i != strlist.length-1) {
                                        newscore += ";";
                                    }
                                }
                                votel.set(findex, newscore);
                                votelist.set("votelist", votel);
                                try{
                                    votelist.save(votelistfile);
                                } catch (IOException e) {
                                    sender.sendMessage("§c在保存投票信息时出现了一个错误: \n" + e.getMessage() + "\n§c请联系服务器管理员解决!");
                                    return false;
                                }
                                sender.sendMessage("§a成功取消投票!");
                                return true;
                            }
                            findex++;
                        }
                        sender.sendMessage("§c你选择的投票不存在(可能已实现或被移除)...");
                    }
                } else {
                    sender.sendMessage("§c你没有权限执行此指令!");
                }
            } else {
                if(sender.hasPermission("vote.help")) {
                    sender.sendMessage("§c指令用法错误! 请输入/vote help查看帮助文档");   
                } else {
                    sender.sendMessage("§c你没有权限执行此指令!");
                }
            }
        } else if(args.length == 2) {
            if (args[0].equals("vote")) {
                if(sender.hasPermission("vote.vote")){
                    File playerlistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "playerlist.yml");
                    FileConfiguration playerlist = YamlConfiguration.loadConfiguration(playerlistfile);
                    String index = playerlist.getString("playerlist." + sender.getName() + ".vote");
                    File votelistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "votelist.yml");
                    FileConfiguration votelist = YamlConfiguration.loadConfiguration(votelistfile);
                    List<String> votel = votelist.getStringList("votelist");
                    if (votel.size() == 0) {
                        sender.sendMessage("§c服务器中尚未发布投票!");
                    } else {
                        boolean isNotFound = true;
                        for(String str : votel) {
                            String index_ = str.split(";")[2];
                            if (index != null && index.equals(index_)) {
                                isNotFound = false;
                                break;
                            }
                        }
                        if (index == null || index.equals("-1") | isNotFound) {
                            int findex = 0;
                            for (String string : votel) {
                                String name = string.split(";")[0];
                                String index_ = string.split(";")[1];
                                String num = string.split(";")[2];
                                if (args[1].equals(name) || args[1].equals(index_)) {
                                    num = String.valueOf((Integer.parseInt(num) + 1));
                                    String votecontent = name + ";" + index_ + ";" + num;
                                    votel.set(findex, votecontent);
                                    votelist.set("votelist", votel);
                                    try {
                                        votelist.save(votelistfile);
                                    } catch (IOException e) {
                                        sender.sendMessage("§c在保存投票信息时出现了一个错误: \n" + e.getMessage() + "\n§c请联系服务器管理员解决!");
                                        return false;
                                    }
                                    playerlist.set("playerlist." + sender.getName() + ".vote", index_);
                                    try {
                                        playerlist.save(playerlistfile);
                                    } catch (IOException e) {
                                        sender.sendMessage("§c在保存投票信息时出现了一个错误: \n" + e.getMessage() + "\n§c请联系服务器管理员解决!");
                                        return false;
                                    }
                                    sender.sendMessage("§a投票成功!");
                                    return true;
                                }
                                findex++;
                            }
                            sender.sendMessage("§c你输入的投票不存在(可能已实现或被移除)...");
                        } else {
                            sender.sendMessage("§c你貌似投过票了...请输入/vote cancel取消后再使用此指令投票");
                        }
                    }
                } else {
                    sender.sendMessage("§c你没有权限执行此指令!");
                }
            } else if (args[0].equals("initiate")) {
                if(sender.hasPermission("vote.initiate")){
                    File votelistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "votelist.yml");
                    FileConfiguration votelist = YamlConfiguration.loadConfiguration(votelistfile);
                    int nextid = votelist.getInt("nextindex");
                    List<String> votel = votelist.getStringList("votelist");
                    votel.add(args[1] + ";" + nextid + ";0");
                    nextid++;
                    votelist.set("nextindex", nextid);
                    votelist.set("votelist", votel);
                    try {
                        votelist.save(votelistfile);
                    } catch (IOException e) {
                        sender.sendMessage("§c在保存投票信息时出现了一个错误: \n" + e.getMessage() + "\n§c请联系服务器管理员解决!");
                        return false;
                    }
                    sender.sendMessage("§a成功创建投票项 §6" + args[1] + "§a!");
                } else {
                    sender.sendMessage("§c你没有权限执行此指令!");
                }
            } else if (args[0].equals("delete")) {
                if(sender.hasPermission("vote.delete")){
                    File votelistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "votelist.yml");
                    FileConfiguration votelist = YamlConfiguration.loadConfiguration(votelistfile);
                    List<String> votel = votelist.getStringList("votelist");
                    int findex = 0;
                    for (String string : votel) {
                        if (args[1].equals(string.split(";")[0]) || args[1].equals(string.split(";")[1])) {
                            votel.remove(findex);
                            votelist.set("votelist", votel);
                            try {
                                votelist.save(votelistfile);
                            } catch (IOException e) {
                                sender.sendMessage("§c在保存投票信息时出现了一个错误: \n" + e.getMessage() + "\n§c请联系服务器管理员解决!");
                                return false;
                            }
                            sender.sendMessage("§c成功删除投票项 §6" + args[1] + "§c!");
                            return true;
                        }
                        findex++;
                    }
                    sender.sendMessage("§c未找到投票项 " + args[1] + "...");
                } else {
                    sender.sendMessage("§c你没有权限执行此指令!");
                }
            } else {
                if(sender.hasPermission("vote.help")){
                    sender.sendMessage("§c指令用法错误! 请输入/vote help查看帮助文档");
                } else{
                    sender.sendMessage("§c你没有权限执行此指令!");
                }
            }
        } else {
            if(sender.hasPermission("vote.help")){
                sender.sendMessage("§c指令用法错误! 请输入/vote help查看帮助文档");
            } else{
                sender.sendMessage("§c你没有权限执行此指令!");
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 1){
            if(sender.hasPermission("vote.help")){
                list.add("help");
            } if(sender.hasPermission("vote.initiate")){
                list.add("initiate");
            } if(sender.hasPermission("vote.delete")){
                list.add("delete");
            } if(sender.hasPermission("vote.vote")){
                list.add("vote");
            } if(sender.hasPermission("vote.get")){
                list.add("get");
            } if(sender.hasPermission("vote.cancel")){
                list.add("cancel");
            } if(sender.hasPermission("vote.list")){
                list.add("list");
            }
        } else if(args.length == 2){
            if(args[0].equals("delete") && sender.hasPermission("vote.delete") || args[0].equals("vote") || sender.hasPermission("vote.vote")){
                File votelistfile = new File(VotingPlugin.getProvidingPlugin(VotingPlugin.class).getDataFolder(), "votelist.yml");
                FileConfiguration votelist = YamlConfiguration.loadConfiguration(votelistfile);
                List<String> votel = votelist.getStringList("votelist");
                for(String str : votel){
                    list.add(str.split(";")[0]);
                }
            }
        }
        return list;
    }
}
