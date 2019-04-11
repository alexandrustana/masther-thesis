package upt.se.utils.store;

import static upt.se.utils.builders.ListBuilder.toList;
import static upt.se.utils.helpers.ClassNames.isEqual;
import static upt.se.utils.helpers.LoggerHelper.LOGGER;
import static upt.se.utils.store.ITypeStore.convert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeReferenceMatch;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import thesis.metamodel.entity.MTypeParameter;
import upt.se.utils.Pair;
import upt.se.utils.builders.ListBuilder;
import upt.se.utils.visitors.VariableBindingVisitor;

public class ITypeBindingStore {

  public static List<ITypeBinding> getAllSubtypes(ITypeBinding typeBinding) {
    return Try.of(() -> convert(typeBinding))
        .flatMap(maybeType -> Option.ofOptional(maybeType).toTry())
        .mapTry(type -> Tuple.of(type, type.newTypeHierarchy(new NullProgressMonitor())))
        .map(tuple -> tuple._2.getAllSubtypes(tuple._1))
        .map(types -> io.vavr.collection.List.of(types))
        .flatMap(list -> Try.of(() -> list.map(type -> convert(type)).map(Option::ofOptional)
            .map(Option::toTry).map(Try::get)))
        .map(list -> list.toJavaList())
        .onFailure(t -> LOGGER.log(Level.SEVERE, "An error has occurred", t))
        .orElse(() -> Try.success(Collections.emptyList())).get();
  }

  public static List<ITypeBinding> getTypeParameterUsages(List<ITypeBinding> declaringClasses,
      List<ITypeBinding> allSubtypes) {
    io.vavr.collection.List<ITypeBinding> declaring = toList(allSubtypes);
    io.vavr.collection.List<ITypeBinding> subtypes = toList(declaringClasses);

    return subtypes
        .filter(subType -> declaring.exists(declared -> toList(declared.getTypeParameters())
            .exists(parameter -> isEqual(parameter, subType))))
        .asJava();
  }

  public static List<ITypeBinding> usagesInDeclaringClass(MTypeParameter entity) {
    return getAllSubtypes(entity.getUnderlyingObject().getDeclaringClass());
  }

  public static List<ITypeBinding> usagesInInheritance(MTypeParameter entity) {
    return Try.of(() -> entity.getUnderlyingObject())
        .map(type -> Tuple.of(type.getDeclaringClass(), type.getSuperclass()))
        .map(tuple -> Tuple.of(getAllSubtypes(tuple._1), getAllSubtypes(tuple._2)))
        .map(tuple -> getTypeParameterUsages(tuple._1, tuple._2))
        .onFailure(t -> LOGGER.log(Level.SEVERE, "An error has occurred", t))
        .orElse(() -> Try.success(Collections.emptyList()))
        .get();
  }


  public static List<ITypeBinding> usagesInVariables(MTypeParameter entity) {
    return Try.of(() -> entity.getUnderlyingObject().getDeclaringClass())
        .map(type -> toList(findVariablesArguments(type)))
        .map(variables -> variables.map(arguments -> arguments.get(getParameterNumber(entity))))
        .map(ListBuilder::toList)
        .onFailure(t -> LOGGER.log(Level.SEVERE, "Could not find parameter in class declaration", t))
        .orElse(() -> Try.success(Collections.emptyList()))
        .get();
  }

  private static int getParameterNumber(MTypeParameter entity) {
    return Try.of(() -> entity.getUnderlyingObject())
        .map(type -> Tuple.of(type, toList(type.getDeclaringClass().getTypeArguments())))
        .map(tuple -> tuple._2.zipWithIndex()
                              .find(argument -> isEqual(argument._1, tuple._1))
                              .map(argument -> argument._2))
        .flatMap(Option::toTry)
        .orElse(() -> Try.success(0))
        .get();
  }

  public static final List<List<ITypeBinding>> findVariablesArguments(ITypeBinding type) {
    List<List<ITypeBinding>> types = new ArrayList<>();

    SearchPattern pattern = SearchPattern.createPattern(type.getJavaElement(),
        IJavaSearchConstants.FIELD_DECLARATION_TYPE_REFERENCE
            | IJavaSearchConstants.LOCAL_VARIABLE_DECLARATION_TYPE_REFERENCE
            | IJavaSearchConstants.CLASS_INSTANCE_CREATION_TYPE_REFERENCE);

    IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
    
    SearchRequestor requestor = new SearchRequestor() {
      public void acceptSearchMatch(SearchMatch match) {
        Try.of(() -> ((TypeReferenceMatch) match))
            .map(TypeReferenceMatch::getElement)
            .filter(element -> element instanceof IMember)
            .map(element -> ((IMember) element).getCompilationUnit())
            .map(compilationUnit -> VariableBindingVisitor.convert(compilationUnit))
            .map(variables -> toList(variables).map(variable -> variable.getType())
                                                .map(type -> toList(type.getTypeArguments()))
                                                .map(arguments -> toList(arguments)))
            .map(ListBuilder::toList)
            .onSuccess(list -> types.addAll(list));
      }
    };

    SearchEngine searchEngine = new SearchEngine();

    try {
      searchEngine.search(pattern,
          new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, 
          scope, requestor, new NullProgressMonitor());
    } catch (CoreException e) {
      LOGGER.log(Level.SEVERE, "An error has occurred while searching", e);
    }

    return types;
  }

}