package au.com.mineauz.groupchat;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncPlayerGroupChatEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean mIsCancelled = false;
	
	private String mMessage;
	private String mFormat;
	private Group mGroup;
	private Player mPlayer;
	
	public AsyncPlayerGroupChatEvent(Player player, String message, String format, Group group)
	{
		super(true);
		mPlayer = player;
		mMessage = message;
		mFormat = format;
		mGroup = group;
	}
	
	@Override
	public boolean isCancelled()
	{
		return mIsCancelled;
	}
	
	@Override
	public void setCancelled( boolean cancelled )
	{
		mIsCancelled = cancelled;
	}
	
	public Player getPlayer()
	{
		return mPlayer;
	}
	
	public String getMessage()
	{
		return mMessage;
	}
	
	public String getFormat()
	{
		return mFormat;
	}
	
	public Group getGroup()
	{
		return mGroup;
	}
	
	
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList() { return handlers; }

}
