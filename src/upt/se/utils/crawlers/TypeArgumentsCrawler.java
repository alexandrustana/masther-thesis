package upt.se.utils.crawlers;

import static upt.se.utils.helpers.Equals.isEqual;
import static upt.se.utils.helpers.LoggerHelper.LOGGER;
import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import io.vavr.collection.List;
import upt.se.utils.visitors.ParameterizedTypeBindingVisitor;

public class TypeArgumentsCrawler {

	public static int getParameterNumber(ITypeBinding actualParameter) {
		return List.of(actualParameter.getDeclaringClass().getTypeParameters()).zipWithIndex()
				.filter(parameter -> isEqual(parameter._1, actualParameter)).map(parameter -> parameter._2).head();
	}

	public static List<List<ITypeBinding>> getUsages(ITypeBinding type) {
		java.util.Set<java.util.List<ITypeBinding>> types = new HashSet<>();

		SearchPattern pattern = SearchPattern.createPattern(type.getJavaElement(),
				IJavaSearchConstants.FIELD_DECLARATION_TYPE_REFERENCE
						| IJavaSearchConstants.LOCAL_VARIABLE_DECLARATION_TYPE_REFERENCE
						| IJavaSearchConstants.CLASS_INSTANCE_CREATION_TYPE_REFERENCE
						| IJavaSearchConstants.PARAMETER_DECLARATION_TYPE_REFERENCE
						| IJavaSearchConstants.RETURN_TYPE_REFERENCE
						| IJavaSearchConstants.SUPERTYPE_TYPE_REFERENCE);

		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		HashSet<ICompilationUnit> processed = new HashSet<>();
		SearchRequestor requestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				Object element = match.getElement();
				if (element instanceof IMember) {
					ICompilationUnit cu = ((IMember) element).getCompilationUnit();
					if (!processed.contains(cu)) {
						processed.add(cu);
						types.addAll(ParameterizedTypeBindingVisitor.convert(cu, type));
					}
				}
			}
		};

		try {
			SearchEngine searchEngine = new SearchEngine();
			searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope,
					requestor, new NullProgressMonitor());
		} catch (CoreException e) {
			LOGGER.log(Level.SEVERE, "An error has occurred while searching", e);
		}

		return List.ofAll(types).map(List::ofAll);
	}

}
