package upt.se.classes;

import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;
import thesis.metamodel.entity.MClass;

@PropertyComputer
public class ApertureCoverage implements IPropertyComputer<Double, MClass> {

	@Override
	public Double compute(MClass entity) {
		return entity.typeParameterPairs().getElements().stream()
			.map(pair -> pair.apertureCoverage())
			.filter(value -> !value.isNaN())
			.mapToDouble(value -> value.doubleValue())
			.min()
			.orElse(Double.NaN);
	}
	
}
