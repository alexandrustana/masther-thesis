package thesis.metamodel.entity;

public interface MClassPair extends ro.lrg.xcore.metametamodel.XEntity {

	@ro.lrg.xcore.metametamodel.ThisIsAProperty
	public java.lang.String toString();

	upt.se.utils.ArgumentPair getUnderlyingObject();
}