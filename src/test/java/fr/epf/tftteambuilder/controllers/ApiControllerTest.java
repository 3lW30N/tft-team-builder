package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.dto.CompDto;
import fr.epf.tftteambuilder.dto.CompMatchDto;
import fr.epf.tftteambuilder.dto.MatchRequest;
import fr.epf.tftteambuilder.models.Comp;
import fr.epf.tftteambuilder.models.Trait;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.models.UnitTrait;
import fr.epf.tftteambuilder.repositories.CompRepository;
import fr.epf.tftteambuilder.repositories.UnitRepository;
import fr.epf.tftteambuilder.repositories.UnitTraitRepository;
import fr.epf.tftteambuilder.services.CompMatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(controllers = {CompController.class, UnitController.class, UnitTraitController.class})
class ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnitRepository unitRepository;

    @MockitoBean
    private CompRepository compRepository;

    @MockitoBean
    private CompMatchService compMatchService;

    @MockitoBean
    private UnitTraitRepository unitTraitRepository;

    @Test
    void getAllCompsReturnsCompositionDtos() throws Exception {
        when(compRepository.findAll()).thenReturn(List.of(comp(7)));

        mockMvc.perform(get("/comp/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].name").value("Arcana"))
                .andExpect(jsonPath("$[0].builds").isArray());
    }

    @Test
    void getCompByIdReturnsCompositionDto() throws Exception {
        when(compRepository.getById(7)).thenReturn(comp(7));

        mockMvc.perform(get("/comp/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Arcana"))
                .andExpect(jsonPath("$.builds").isArray());
    }

    @Test
    void getAllUnitsReturnsUnitsAsJson() throws Exception {
        Unit unit = new Unit();
        unit.setId("Ahri");
        unit.setName("Ahri");
        unit.setCost(4);
        when(unitRepository.findAll()).thenReturn(List.of(unit));

        mockMvc.perform(get("/unit/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("Ahri"))
                .andExpect(jsonPath("$[0].name").value("Ahri"))
                .andExpect(jsonPath("$[0].cost").value(4));
    }

    @Test
    void getUnitCostReturnsCostAsJsonNumber() throws Exception {
        Unit unit = new Unit();
        unit.setId("Ahri");
        unit.setCost(4);
        when(unitRepository.findById("Ahri")).thenReturn(Optional.of(unit));

        mockMvc.perform(get("/unit/{id}/cost", "Ahri"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(4));
    }

    @Test
    void getAllUnitTraitsGroupsTraitIdsByUnit() throws Exception {
        when(unitTraitRepository.findAll()).thenReturn(List.of(
                relation(unit("Ahri"), trait("Arcana")),
                relation(unit("Ahri"), trait("Sorcerer")),
                relation(unit("Syndra"), trait("Sorcerer"))
        ));

        mockMvc.perform(get("/traits/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unitId").value("Ahri"))
                .andExpect(jsonPath("$[0].traitIds[0]").value("Arcana"))
                .andExpect(jsonPath("$[0].traitIds[1]").value("Sorcerer"))
                .andExpect(jsonPath("$[1].unitId").value("Syndra"))
                .andExpect(jsonPath("$[1].traitIds[0]").value("Sorcerer"));
    }

    @Test
    void getTraitsByUnitReturnsTraitList() throws Exception {
        when(unitTraitRepository.findTraitsByUnitId("Ahri")).thenReturn(List.of(trait("Arcana")));

        mockMvc.perform(get("/traits/{unitId}", "Ahri"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("Arcana"));
    }

    @Test
    void getUnitsByTraitReturnsUnitList() throws Exception {
        when(unitTraitRepository.findUnitsByTraitId("Arcana")).thenReturn(List.of(unit("Ahri")));

        mockMvc.perform(get("/traits/units/{traitId}", "Arcana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("Ahri"));
    }

    @Test
    void matchAcceptsBoardAndReturnsRankedCompositionJson() throws Exception {
        CompDto comp = new CompDto(7, "Arcana", 3.2, 0.15, 2.0, "Level 8", List.of());
        CompMatchDto match = new CompMatchDto(
                comp, 82, List.of("Ahri"), List.of("Syndra"), List.of("You have 1/2 of the comp's units"));
        when(compMatchService.rank(new MatchRequest(
                List.of(new MatchRequest.BoardUnit("Ahri", 2)),
                List.of("Deathblade")
        ))).thenReturn(List.of(match));

        mockMvc.perform(post("/comp/match")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "units": [{"unitId": "Ahri", "stars": 2}],
                                  "itemIds": ["Deathblade"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").value(82))
                .andExpect(jsonPath("$[0].comp.id").value(7))
                .andExpect(jsonPath("$[0].comp.name").value("Arcana"))
                .andExpect(jsonPath("$[0].matchedUnits[0]").value("Ahri"))
                .andExpect(jsonPath("$[0].missingUnits[0]").value("Syndra"))
                .andExpect(jsonPath("$[0].reasons[0]").value("You have 1/2 of the comp's units"));

        verify(compMatchService).rank(new MatchRequest(
                List.of(new MatchRequest.BoardUnit("Ahri", 2)),
                List.of("Deathblade")
        ));
    }

    @Test
    void matchRejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/comp/match")
                        .contentType(APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());
    }

    private static Comp comp(int id) {
        Comp comp = new Comp();
        comp.setId(id);
        comp.setName("Arcana");
        comp.setBuilds(List.of());
        return comp;
    }

    private static Unit unit(String id) {
        Unit unit = new Unit();
        unit.setId(id);
        unit.setName(id);
        return unit;
    }

    private static Trait trait(String id) {
        Trait trait = new Trait();
        trait.setId(id);
        trait.setName(id);
        return trait;
    }

    private static UnitTrait relation(Unit unit, Trait trait) {
        UnitTrait relation = new UnitTrait();
        relation.setUnit(unit);
        relation.setTrait(trait);
        return relation;
    }
}
