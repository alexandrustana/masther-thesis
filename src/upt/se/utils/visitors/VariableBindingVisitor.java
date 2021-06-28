package upt.se.utils.visitors;

import static upt.se.utils.helpers.Equals.isEqual;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import upt.se.utils.Parser;

public class VariableBindingVisitor extends ASTVisitor {

  private static Map<ICompilationUnit, HashSet<IVariableBinding>> allVariableBindings =
      new HashMap<>();
  private HashSet<IVariableBinding> attributeBindings = new HashSet<>();
  private HashSet<ITypeBinding> returnTypes = new HashSet<>();
  private ITypeBinding searchedType;

  private VariableBindingVisitor(ITypeBinding searchedType) {
    this.searchedType = searchedType;
  }

  public boolean visit(SimpleName node) {
    IBinding binding = node.resolveBinding();
    if (binding instanceof IVariableBinding) {
      IVariableBinding variable = (IVariableBinding) binding;
      if (!variable.getType().isRawType() && isEqual(variable.getType().getErasure(), searchedType)) {
        attributeBindings.add(variable);
      }
    } else if(binding instanceof IMethodBinding) {
    	IMethodBinding method = (IMethodBinding) binding;
        if (!method.getReturnType().isRawType() && isEqual(method.getReturnType().getErasure(), searchedType)) {
        	returnTypes.add(((IMethodBinding) binding).getReturnType());
        }
    }
    return super.visit(node);
  }

  private HashSet<IVariableBinding> getAttributeBindings() {
    return attributeBindings;
  }

  public static Tuple2<HashSet<IVariableBinding>, HashSet<ITypeBinding>> convert(ICompilationUnit unit,
      ITypeBinding searchedType) {

    VariableBindingVisitor self = new VariableBindingVisitor(searchedType);

    CompilationUnit cUnit = (CompilationUnit) Parser.parse(unit);
    cUnit.accept(self);

    allVariableBindings.put(unit, self.getAttributeBindings());
    return Tuple.of(allVariableBindings.get(unit), self.returnTypes);
  }
}
