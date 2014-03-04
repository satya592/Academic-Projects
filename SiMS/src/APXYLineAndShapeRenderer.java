import java.awt.Shape;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.data.xy.XYDataset;

public class APXYLineAndShapeRenderer extends XYStepAreaRenderer {
	private static final long serialVersionUID = 1L; // <- eclipse insists on
														// this and I hate
														// warnings ^^

	APXYLineAndShapeRenderer(boolean lines, boolean shapes) {
		// super(lines, shapes);
	}

	@Override
	protected void addEntity(EntityCollection entities, Shape area,
			XYDataset dataset, int series, int item, double entityX,
			double entityY) {
		// if (area.getBounds().width < 2 || area.getBounds().height < 2)
		// super.addEntity(entities, null, dataset, series, item, entityX,
		// entityY);
		// else
		super.addEntity(entities, null, dataset, series, item, entityX, entityY);
	}
}