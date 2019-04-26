package upt.se.arguments.pair;

import static io.vavr.API.For;
import static upt.se.utils.helpers.Equals.isEqual;
import static upt.se.utils.helpers.LoggerHelper.LOGGER;
import java.util.function.Function;
import java.util.logging.Level;
import org.eclipse.jdt.core.dom.ITypeBinding;
import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.control.Try;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;
import thesis.metamodel.entity.MArgumentType;
import thesis.metamodel.entity.MTypePair;
import thesis.metamodel.factory.Factory;
import upt.se.utils.TypePair;
import upt.se.utils.crawlers.InheritanceArgumentTypes;
import upt.se.utils.crawlers.VariablesArgumentTypes;
import upt.se.utils.helpers.GroupBuilder;

@RelationBuilder
public class UsedArgumentsTypes implements IRelationBuilder<MTypePair, MTypePair> {

  @Override
  public Group<MTypePair> buildGroup(MTypePair entity) {
    return For(
        getArgumentsTypes(InheritanceArgumentTypes::getUsages, entity),
        getArgumentsTypes(VariablesArgumentTypes::getUsages, entity))
            .yield((declaringClasses, attributeDeclarations) -> declaringClasses
                .appendAll(attributeDeclarations))
            .map(usedArguments -> usedArguments
                .map(arguments -> Tuple.of(arguments.head(), arguments.tail().head())))
            .map(pairs -> pairs.distinctBy((p1, p2) -> isEqual(p1, p2) ? 0 : 1))
            .map(pairs -> pairs.map(pair -> new TypePair(pair._1, pair._2))
                .map(Factory.getInstance()::createMTypePair))
            .onFailure(t -> LOGGER.log(Level.SEVERE, "An error has occurred", t))
            .orElse(() -> Try.success(List.empty()))
            .map(GroupBuilder::wrap)
            .get();
  }

  private Try<List<List<ITypeBinding>>> getArgumentsTypes(
      Function<MArgumentType, List<ITypeBinding>> getUsages, MTypePair entity) {
    return Try.of(() -> entity.getUnderlyingObject())
        .map(parameters -> Tuple.of(parameters.getFirst(), parameters.getSecond()))
        .map(pair -> pair.map(Factory.getInstance()::createMArgumentType,
            Factory.getInstance()::createMArgumentType))
        .map(pair -> pair.map(getUsages, getUsages))
        .map(pairUsages -> pairUsages._1.zip(pairUsages._2))
        .map(usages -> usages.map(tuple -> List.of(tuple._1, tuple._2)));
  }

}