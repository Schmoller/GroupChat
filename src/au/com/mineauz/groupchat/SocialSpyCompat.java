package au.com.mineauz.groupchat;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.earth2me.essentials.Essentials;

public class SocialSpyCompat implements Listener
{
	private Essentials mEssentials;
	private SocialSpyCompat()
	{
		mEssentials = (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");
		Bukkit.getPluginManager().registerEvents(this, GroupChat.instance);
	}
	
	public boolean getSocialSpyState(Player player)
	{
		return mEssentials.getUser(player).isSocialSpyEnabled();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onGroupChat(AsyncPlayerGroupChatEvent event)
	{
		Set<Player> members = event.getGroup().getMembers();
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(members.contains(player) || event.getPlayer().equals(player))
				continue;
			
			if(getSocialSpyState(player))
				player.sendMessage(ChatColor.RED + "[GROUP]" + ChatColor.WHITE + String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()));
		}
	}
	
	public static void initialize()
	{
		if(Bukkit.getPluginManager().isPluginEnabled("Essentials"))
			new SocialSpyCompat();
	}
}
