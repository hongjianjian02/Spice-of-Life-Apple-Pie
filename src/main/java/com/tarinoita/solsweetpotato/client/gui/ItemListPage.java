/* Modified 2026-11-20 by the hongjianjian02 fork: lay out one icon per entry. */
package com.tarinoita.solsweetpotato.client.gui;

import com.tarinoita.solsweetpotato.client.gui.elements.UIItemStack;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class ItemListPage extends Page {
	protected static final int itemsPerRow = 5;
	protected static final int rowsPerPage = 6;
	protected static final int itemsPerPage = itemsPerRow * rowsPerPage;
	protected static final int itemSpacing = UIItemStack.size + 4;
	
	protected ItemListPage(Rectangle frame, String header, List<ItemStack> items) {
		this(frame, header, items, UIItemStack::new);
	}

	<T> ItemListPage(Rectangle frame, String header, List<T> items, Function<T, UIItemStack> createIcon) {
		super(frame, header);
		
		int minX = (1 - itemsPerRow) * itemSpacing / 2;
		int minY = (1 - rowsPerPage) * itemSpacing / 2 - 4;
		
		for (int i = 0; i < items.size(); i++) {
			int x = minX + itemSpacing * (i % itemsPerRow);
			int y = minY + itemSpacing * ((i / itemsPerRow) % rowsPerPage);
			
			UIItemStack view = createIcon.apply(items.get(i));
			view.setCenterX(getCenterX() + x);
			view.setCenterY(getCenterY() + y);
			children.add(view);
		}
	}
}
