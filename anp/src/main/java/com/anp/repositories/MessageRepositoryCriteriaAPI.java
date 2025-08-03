package com.anp.repositories;

import com.anp.domain.entities.Message;
import com.anp.domain.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageRepositoryCriteriaAPI {
    @PersistenceContext
    private EntityManager entityManager;
    
    public List<Message> findAllMessagesBetweenTwoUsersWithCriteria(String firstUserId, String secondUserId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Message> query = cb.createQuery(Message.class);
        Root<Message> message = query.from(Message.class);

        // First condition: fromUser.id = firstUserId AND toUser.id = secondUserId
        Predicate condition1 = cb.and(
                cb.equal(message.get("fromUser").get("id"), firstUserId),
                cb.equal(message.get("toUser").get("id"), secondUserId)
        );

        // Second condition: fromUser.id = secondUserId AND toUser.id = firstUserId
        Predicate condition2 = cb.and(
                cb.equal(message.get("fromUser").get("id"), secondUserId),
                cb.equal(message.get("toUser").get("id"), firstUserId)
        );

        // Combine both conditions with OR
        Predicate finalCondition = cb.or(condition1, condition2);

        query.where(finalCondition);
        query.orderBy(cb.asc(message.get("time")));

        return entityManager.createQuery(query).getResultList();
    }
    
    @Transactional
    public void updateStatusFromReadMessagesWithCriteria(String toUserId, String fromUserId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Message> update = cb.createCriteriaUpdate(Message.class);
        Root<Message> message = update.from(Message.class);

        // Set status to 1
        update.set(message.get("status"), 1);

        // Add conditions
        Predicate condition = cb.and(
                cb.equal(message.get("toUser").get("id"), toUserId),
                cb.equal(message.get("fromUser").get("id"), fromUserId),
                cb.equal(message.get("status"), 0)
        );

        update.where(condition);

        entityManager.createQuery(update).executeUpdate();
    }
    
    public List<Message> getAllUnreadMessagesWithCriteria(String loggedInUserId) {
        // This implementation requires a different approach due to limitations
        // in the Criteria API for complex subqueries with aggregates
        
        // First, get the most recent message time for each sender
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> subQuery = cb.createQuery(Object[].class);
        Root<Message> subRoot = subQuery.from(Message.class);
        
        subQuery.multiselect(
            subRoot.get("fromUser").get("id"),
            cb.greatest(subRoot.get("time"))
        );
        
        subQuery.where(cb.equal(subRoot.get("toUser").get("id"), loggedInUserId));
        subQuery.groupBy(subRoot.get("fromUser").get("id"));
        
        List<Object[]> latestMessageTimes = entityManager.createQuery(subQuery).getResultList();
        
        // Now build a query to get the actual messages
        List<Message> result = new ArrayList<>();
        
        for (Object[] entry : latestMessageTimes) {
            String fromUserId = (String) entry[0];
            LocalDateTime maxTime = (LocalDateTime) entry[1];
            
            CriteriaQuery<Message> messageQuery = cb.createQuery(Message.class);
            Root<Message> messageRoot = messageQuery.from(Message.class);
            
            messageQuery.where(
                cb.and(
                    cb.equal(messageRoot.get("fromUser").get("id"), fromUserId),
                    cb.equal(messageRoot.get("toUser").get("id"), loggedInUserId),
                    cb.equal(messageRoot.get("time"), maxTime)
                )
            );
            
            Message message = entityManager.createQuery(messageQuery).getSingleResult();
            result.add(message);
        }
        
        // Sort by time in descending order
        result.sort((m1, m2) -> m2.getTime().compareTo(m1.getTime()));
        
        return result;
    }
    
    public List<Object[]> getCountOfUnreadMessagesByFromUserWithCriteria(String loggedInUserId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Message> message = query.from(Message.class);

        query.multiselect(
                message.get("fromUser").get("id"),
                cb.count(message)
        );

        query.where(cb.and(
                cb.equal(message.get("status"), 0),
                cb.equal(message.get("toUser").get("id"), loggedInUserId)
        ));

        query.groupBy(message.get("fromUser").get("id"));
        
        // Order by time requires an aggregate function since we're grouping
        // We need to use max(time) to order by the most recent message
        query.orderBy(cb.desc(cb.greatest(message.get("time"))));

        return entityManager.createQuery(query).getResultList();
    }
}