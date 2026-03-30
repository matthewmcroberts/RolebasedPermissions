package com.matthewmcroberts.rankmanager.api;

import com.matthewmcroberts.rankmanager.dto.DisplayNameUpdate;
import com.matthewmcroberts.rankmanager.dto.InheritanceUpdate;
import com.matthewmcroberts.rankmanager.dto.NewRank;
import com.matthewmcroberts.rankmanager.dto.PermissionsUpdate;
import com.matthewmcroberts.rankmanager.dto.PlayerRankAssignment;
import com.matthewmcroberts.rankmanager.dto.PriorityUpdate;
import com.matthewmcroberts.rankmanager.dto.Rank;
import com.matthewmcroberts.rankmanager.dto.RankAssignment;
import com.matthewmcroberts.rankmanager.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/secure/api")
@RequiredArgsConstructor
public class RankManagerController {
    private final RankService rankService;

    /** POST /secure/api/ranks */
    @PostMapping("/ranks")
    @ResponseStatus(HttpStatus.CREATED)
    public Rank createRank(@RequestBody NewRank body) {
        return rankService.createRank(
                body.getRankId(),
                body.getDisplayName(),
                body.getPriority(),
                body.getOwnPermissions(),
                body.getEffectivePermissions(),
                body.getInheritedRankIds());
    }

    /** GET /secure/api/ranks/{id} */
    @GetMapping("/ranks/{id}")
    public Rank getRankById(@PathVariable String id) {
        return rankService.getRankById(id);
    }

    /** GET /secure/api/ranks/by-name/{name} */
    @GetMapping("/ranks/by-name/{name}")
    public Rank getRankByName(@PathVariable String name) {
        return rankService.getRankByName(name);
    }

    /** GET /secure/api/ranks */
    @GetMapping("/ranks")
    public List<Rank> getAllRanks() {
        return rankService.getAllRanks();
    }

    /** PATCH /secure/api/ranks/{id}/display-name */
    @PatchMapping("/ranks/{id}/display-name")
    public Rank updateDisplayName(
            @PathVariable String id,
            @RequestBody DisplayNameUpdate body) {
        return rankService.updateRankDisplayName(id, body.getDisplayName());
    }

    /** PATCH /secure/api/ranks/{id}/priority */
    @PatchMapping("/ranks/{id}/priority")
    public Rank updatePriority(
            @PathVariable String id,
            @RequestBody PriorityUpdate body) {
        return rankService.updateRankPriority(id, body.getPriority());
    }

    /** POST /secure/api/ranks/{id}/permissions */
    @PostMapping("/ranks/{id}/permissions")
    public Rank addPermissions(
            @PathVariable String id,
            @RequestBody PermissionsUpdate body) {
        return rankService.addRankPermissions(id, body.getPermissions());
    }

    /** DELETE /secure/api/ranks/{id}/permissions */
    @DeleteMapping("/ranks/{id}/permissions")
    public Rank removePermissions(
            @PathVariable String id,
            @RequestBody PermissionsUpdate body) {
        return rankService.removeRankPermissions(id, body.getPermissions());
    }

    /** POST /secure/api/ranks/{id}/inherited-ranks */
    @PostMapping("/ranks/{id}/inherited-ranks")
    public Rank addInheritance(
            @PathVariable String id,
            @RequestBody InheritanceUpdate body) {
        return rankService.addRankInheritance(id, body.getInheritedRankIds());
    }

    /** DELETE /secure/api/ranks/{id}/inherited-ranks */
    @DeleteMapping("/ranks/{id}/inherited-ranks")
    public Rank removeInheritance(
            @PathVariable String id,
            @RequestBody InheritanceUpdate body) {
        return rankService.removeRankInheritance(id, body.getInheritedRankIds());
    }

    /** DELETE /secure/api/ranks/{id} */
    @DeleteMapping("/ranks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRank(@PathVariable String id) {
        rankService.deleteRank(id);
    }

    /** GET /secure/api/ranks/{rankId}/players */
    @GetMapping("/ranks/{rankId}/players")
    public List<PlayerRankAssignment> getPlayersWithRank(@PathVariable String rankId) {
        return rankService.getPlayerRankAssignmentsWithRank(rankId);
    }

    /** POST /secure/api/players/{playerId}/rank */
    @PostMapping("/players/{playerId}/rank")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerRankAssignment assignRank(
            @PathVariable String playerId,
            @RequestBody RankAssignment body) {

        return rankService.assignPlayerRank(
                playerId,
                body.getAssignedById(),
                body.getRankId());
    }

    /** GET /secure/api/players/{playerId}/rank-assignments */
    @GetMapping("/players/{playerId}/rank-assignments")
    public List<PlayerRankAssignment> getPlayerRankAssignments(
            @PathVariable String playerId) {
        return rankService.getPlayerRankAssignments(playerId);
    }

    /** DELETE /secure/api/players/{playerId}/rank */
    @DeleteMapping("/players/{playerId}/rank")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePlayerRank(@PathVariable String playerId) {
        rankService.removePlayerRank(playerId);
    }

    /** GET /secure/api/players/{playerId}/ranks */
    @GetMapping("/players/{playerId}/ranks")
    public List<Rank> getPlayerRanks(
            @PathVariable String playerId) {
        return rankService.getPlayerRanks(playerId);
    }
}