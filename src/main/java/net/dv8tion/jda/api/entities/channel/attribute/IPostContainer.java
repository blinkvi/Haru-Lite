/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.api.entities.channel.attribute;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.managers.channel.attribute.IPostContainerManager;

public interface IPostContainer extends IThreadContainer {

	int MAX_POST_CONTAINER_TOPIC_LENGTH = 4096;

	int MAX_POST_TAGS = 5;

	@Nonnull
	@Override
	IPostContainerManager<?, ?> getManager();

	@Nullable
	String getTopic();

	default boolean isTagRequired() {
		return getFlags().contains(ChannelFlag.REQUIRE_TAG);
	}

	@Nullable
	EmojiUnion getDefaultReaction();

	@Nonnull
	SortOrder getDefaultSortOrder();

	enum SortOrder {

		RECENT_ACTIVITY(0),

		CREATION_TIME(1),

		UNKNOWN(-1),;

		private final int order;

		SortOrder(int order) {
			this.order = order;
		}

		public int getKey() {
			return order;
		}

		@Nonnull
		public static SortOrder fromKey(int key) {
			for (SortOrder order : values()) {
				if (order.order == key)
					return order;
			}

			return UNKNOWN;
		}
	}
}
