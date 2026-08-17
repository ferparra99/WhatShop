---
name: sk-ai-council
description: "AI Council: multi-seat planning deliberation, artifact persistence, convergence checks, and packet-local ai-council outputs. Use for multi-model strategy comparison, architecture decisions, and structured planning."
---

# AI Council

Planning-only council deliberation with diverse seats, convergence checks, and packet-local `ai-council/**` artifact persistence.

---

## 1. WHAT IT DOES

The AI Council convenes multiple "seats" (different reasoning lenses/perspectives) to deliberate on a planning question, critique each other's proposals, check for convergence, and produce structured artifacts — all without modifying application code.

### Use Cases
- Compare two or more implementation plans
- Ask multiple reasoning lenses to critique a proposed architecture
- Decide whether a plan has enough agreement to proceed
- Persist council reports as packet-local artifacts

---

## 2. HOW IT WORKS

### Council Workflow

```
STEP 1: RESOLVE & PREPARE
  - Identify the target spec folder or question
  - Select 2-3 distinct reasoning seats (e.g., conservative, exploratory, pragmatic)

STEP 2: DELIBERATE & CONVERGE
  - Each seat produces an independent proposal
  - Cross-seat critique (adversarial review)
  - Apply convergence check: 2-of-3 agreement or identify remaining disagreements

STEP 3: PERSIST & HANDOFF
  - Produce council report with recommendations
  - Save artifacts to ai-council/ folder
  - Hand off planning result to implementation
```

### Seat Diversity Examples

| Seat | Lens | Focus |
|---|---|---|
| **Architect** | Conservative, standards-first | Scalability, maintainability, patterns |
| **Explorer** | Novel, creative | New approaches, unconventional solutions |
| **Reviewer** | Skeptical, risk-aware | Edge cases, failure modes, trade-offs |

---

## 3. ACTIVATION

### Via tool skill
When you need a council deliberation, say so explicitly:
- "Convene the AI council to evaluate this plan"
- "Run a council deliberation on this architecture"
- "I need multi-seat planning advice"

### Artifacts produced
Council outputs go into `ai-council/` folder with:
- `report.md` — Final council report with recommendations
- `proposals/` — Individual seat proposals
- `state.jsonl` — Append-only deliberation state

---

## 4. RULES

### ALWAYS
- Keep council writes scoped to `ai-council/` artifacts only
- Preserve the planning-only boundary (no code changes)
- Label simulated vantages honestly
- Append a `council_complete` event for completed runs

### NEVER
- Write application code or modify spec docs during council
- Claim an external AI participated unless it actually ran
- Rewrite historical state — append only

---

## 5. EXAMPLE USAGE

**Simple council invocation:**
```
/skill sk-ai-council
I need the AI council to evaluate this plan for our product search feature:

Current approach: SQL full-text search
Proposed: Elasticsearch integration

Please convene 3 seats to compare approaches.
```

**Multi-seat planning:**
```
/skill sk-ai-council
Convene a council with Architect (API-first), Explorer (GraphQL), 
and Reviewer (security-focused) seats to advise on our new 
checkout microservice design.
```
