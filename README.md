# BudgetActivity - Musembi Muia

## Overview
This module handles the Budget Management screen of the BudgetingApp.
It allows users to create, view, edit and delete category budgets
and provides a monthly spending overview.

## Files Implemented
| File | Description |
|------|-------------|
| `BudgetActivity.java` | Main budget screen logic |
| `BudgetAdapter.java` | RecyclerView adapter for budget cards |
| `Budget.java` | Room entity model for budgets |
| `BudgetDao.java` | Database operations for budgets |
| `CategoryDao.java` | Database operations for categories |
| `TransactionDao.java` | Stubbed — returns 0 until team merges |
| `AppDatabase.java` | Room database singleton |
| `activity_budget.xml` | Main budget screen layout |
| `item_budget.xml` | Individual budget card layout |
| `dialog_create_budget.xml` | Create/Edit budget dialog layout |

## Features
- Monthly overview card showing total budget vs total spent
- List of category budgets with blue progress bars
- Colour-coded progress: blue (safe), yellow (70%+), red (90%+)
- Create new budgets with category, amount and period
- Tap any budget to edit or delete
- Delete confirmation dialog to prevent accidents

## Notes for Team Integration
- `TransactionDao.getSpentByCategory()` is stubbed and returns 0
  Replace with real implementation after team merges transactions table
- Seed categories block in `BudgetActivity.onCreate()` should be
  removed after Eugine's CategoryDao is integrated
- `AppDatabase.java` is a standalone version — replace with
  team's master AppDatabase that includes all entities

## Theme
- Primary colour: `#1565C0` (Blue)
- All progress bars use primary blue as specified
```

---

