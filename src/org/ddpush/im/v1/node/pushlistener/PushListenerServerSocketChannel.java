package org.ddpush.im.v1.node.pushlistener;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

import io.netty.channel.ChannelException;
import io.netty.channel.socket.DefaultServerSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class PushListenerServerSocketChannel extends NioServerSocketChannel {
	private final ServerSocketChannelConfig config;
	private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
	private static ServerSocketChannel newSocket(SelectorProvider provider) {
        try {
            return provider.openServerSocketChannel();
        } catch (IOException e) {
            throw new ChannelException(
                    "Failed to open a server socket.", e);
        }
    }
	/**
     * Create a new instance
     */
    public PushListenerServerSocketChannel() {
        this(newSocket(DEFAULT_SELECTOR_PROVIDER));
    }

    /**
     * Create a new instance using the given {@link SelectorProvider}.
     */
    public PushListenerServerSocketChannel(SelectorProvider provider) {
        this(newSocket(provider));
    }

	public PushListenerServerSocketChannel(ServerSocketChannel channel) {
		super(channel);
		this.config = new NioServerSocketChannelConfig(this, javaChannel()
				.socket());
	}

	@Override
	public ServerSocketChannelConfig config() {
		return config;
	}

	private final class NioServerSocketChannelConfig extends
			DefaultServerSocketChannelConfig {
		private NioServerSocketChannelConfig(NioServerSocketChannel channel,
				ServerSocket javaSocket) {
			super(channel, javaSocket);
			this.setMaxMessagesPerRead(1);
		}

		@Override
		protected void autoReadCleared() {
			setReadPending(false);
		}
	}
}
