package io.recheck.uuidprotocol.nodenetwork.datasource;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.nodenetwork.model.UUStatements;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UUStatementsDataSource extends AuditDataSource<UUStatements> {
    public UUStatementsDataSource() {
        super(UUStatements.class);
    }

    public UUStatements find(String subject, String predicate, String object) {
        Filter filter = Filter.and(Filter.equalTo("subject", subject),
                                    Filter.equalTo("predicate", predicate),
                                    Filter.equalTo("object", object),
                                    Filter.equalTo("softDeleted", false));
        Optional<UUStatements> firstNodeOptional = where(filter).stream().findFirst();
        return firstNodeOptional.orElse(null);
    }

    public List<UUStatements> findBySubjectAndPredicate(String subject, String predicate) {
        Filter filter = Filter.and(Filter.equalTo("subject", subject), Filter.equalTo("predicate", predicate));
        return where(filter);
    }

    public List<UUStatements> findByPredicate(String predicate) {
        return whereEqualTo("predicate", predicate);
    }
}
