package com.ssafy.tripservice.api.service;

import com.mongodb.client.result.DeleteResult;
import com.ssafy.tripservice.api.request.PlanModifyRequest;
import com.ssafy.tripservice.api.request.PlanRequest;
import com.ssafy.tripservice.api.response.PlanResponse;
import com.ssafy.tripservice.api.response.TripResponse;
import com.ssafy.tripservice.db.entity.Plan;
import com.ssafy.tripservice.db.entity.Trip;
import com.ssafy.tripservice.db.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PlanServiceImpl implements PlanService{
    private final MongoOperations mongoTemplate;
    private final PlanRepository planRepository;
    @Override
    public Optional<PlanResponse> createPlan(PlanRequest planRequest) {
        planRequest.setLastModifiedDate(LocalDate.now());
        Plan plan = planRequest.toEntity();

        try {
            return Optional.of(planRepository.insert(plan))
                    .map(Plan::toPlanResponse);
        } catch (DataAccessException e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public Optional<PlanResponse> modifyPlan(PlanModifyRequest planRequest) {
        Optional<Plan> plan = Optional.ofNullable(mongoTemplate.findById(planRequest.getPlanId(), Plan.class));
        if(plan.isEmpty()){
            log.error("Plan being modified is not found....");
            return Optional.empty();
        }

        Query query = new Query(Criteria.where("_id").is(planRequest.getPlanId()));
        Update update = new Update();

        update.set("planName", planRequest.getPlanName());
        update.set("planDesc", planRequest.getPlanDesc());
        update.set("lastModifiedDate", LocalDate.now());
        update.set("participantIds", planRequest.getParticipantIds());
        update.set("planInfo", planRequest.getPlanInfo());

        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, Plan.class))
                .map(Plan::toPlanResponse);
    }

    @Override
    public Optional<PlanResponse> getPlanById(UUID planId) {
        return Optional.ofNullable(mongoTemplate.findById(planId, Plan.class))
                .map(Plan::toPlanResponse);
    }

    @Override
    public boolean deletePlan(UUID planId) {
        Query query = new Query(Criteria.where("_id").is(planId));
        DeleteResult res = mongoTemplate.remove(query, Plan.class);
        if(res.getDeletedCount() == 0)
            log.error("Plan being deleted is not found....");
        return false;
    }
}
