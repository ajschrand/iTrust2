package edu.ncsu.csc.iTrust2.services.security;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.security.LogEntry;
import edu.ncsu.csc.iTrust2.repositories.security.LogEntryRepository;
import edu.ncsu.csc.iTrust2.services.Service;

/**
 * Service for interacting with LogEntries. Used by the LoggerUtil class.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class LogEntryService extends Service<LogEntry, Long> {

    /**
     * LogEntry repository, for CRUD tasks
     */
    @Autowired
    private LogEntryRepository repository;

    @Override
    protected JpaRepository<LogEntry, Long> getRepository () {
        return repository;
    }

    /**
     * Finds all LogEntries for a given user, where they are the primary or
     * secondary user
     *
     * @param user
     *            User to find entries for
     * @return All matching LogEntries
     */
    public List<LogEntry> findAllForUser ( final String user ) {
        return repository.findByPrimaryUserOrSecondaryUser( user );
    }

    /**
     * Finds LogEntries for a user within a provided date range.
     * 
     * @param user
     *            User to find entries for. Both dates inclusive.
     * @param startDate
     *            Start date
     * @param endDate
     *            End date
     * @return Matching LogEntries
     */
    public List<LogEntry> findByDateRange ( final String user, final ZonedDateTime startDate,
            final ZonedDateTime endDate ) {

        final List<LogEntry> withinRange = repository.findByTimeBetween( startDate, endDate );

        return withinRange.stream()
                .filter( e -> e.getPrimaryUser().equals( user ) || e.getSecondaryUser().equals( user ) )
                .collect( Collectors.toList() );

    }

}
