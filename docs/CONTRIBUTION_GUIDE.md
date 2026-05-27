# Git and GitHub Guide for Contributors

A step-by-step guide for contributing to LFRS Group 4 OOP projects using Git and GitHub. Follow this workflow for all your contributions.

## Prerequisites
Before you start, make sure you have:
- Git installed on your machine
- A GitHub account
- Access to the project repository
- The repository URL you want to contribute to

## Git Workflow Overview
Here's the typical workflow you'll follow for every contribution:
1. Fork the repository (if you don't have write access)
2. Clone the repository to your local machine
3. Create a new branch for your work
4. Make your changes
5. Stage and commit your changes
6. Push your changes to GitHub
7. Create a pull request
8. Wait for review and merge

---

## Step 1: Clone the Repository
**Purpose:** Download a local copy of the repository to your computer.

**Command:**
```bash
git clone <repository-url>
```

**Example:**
```bash
git clone https://github.com/your-username/repository-name.git
```

## Step 2: Navigate to the Repository Folder
**Purpose:** Move into the repository folder so you can run Git commands.

**Command:**
```bash
cd <repository-folder-name>
```

## Step 3: Set Remote URL (if needed)
**Purpose:** Link your local repository with your remote GitHub repository.

**Command:**
```bash
git remote add origin <url>
```

---

## Branching & Git Workflow

### Branch Naming Convention
| Branch Type | Naming Convention | Example |
| :--- | :--- | :--- |
| **Main** | `main` | `main` |
| **Development** | `dev` | `dev` |
| **Feature** | `feature/feature-name` | `feature/add-auth` |
| **Bugfix** | `bugfix/issue-name` | `bugfix/fix-footer` |
| **Hotfix** | `hotfix/critical-fix` | `hotfix/fix-login-crash` |

### 1. Switch to develop branch
Always start from the latest development branch.
```bash
git checkout dev
git pull origin dev
```

### 2. Create a feature branch
Keep your work separate from the main branch.
```bash
git checkout -b feature/feature-name
```

### 3. Make your changes in the code
Add or modify files as needed. Save your files when you're done.

### 4. Check Status
```bash
git status
```

### 5. Stage Changes
```bash
git add .
# OR
git add <file-name>
```

---

## Commit Message Guidelines

### Commit Message Format
`<type>(<scope>): <description>`

### Allowed Commit Types (Conventional Commits)
| Type | Description |
| :--- | :--- |
| **feat** | A new feature |
| **fix** | A bug fix |
| **docs** | Documentation changes |
| **style** | Code style changes (formatting, etc.) |
| **refactor** | Code changes that neither fix a bug nor add a feature |
| **perf** | Performance improvements |
| **test** | Adding or modifying tests |
| **chore** | Maintenance and other minor tasks |

### 6. Commit your changes
```bash
git commit -m "<type>(<scope>): <description>"
```
*Example: `git commit -m "feat(auth): add login authentication"`*

---

## Step 7: Pull Latest Changes (Important!)
Make sure your branch is up to date with the `dev` branch before pushing.
```bash
git pull origin dev
```

## Step 8: Push Changes
```bash
git push origin feature/feature-name
```

---

## Pull Request Guidelines

### Step 9: Create a Pull Request (PR)
1. Go to your repository on GitHub.
2. Click **Compare & pull request**.
3. Select your branch and merge into `dev` (not `main`).
4. Use the PR template for your description.

### PR Title Format
`<type>(<scope>): <short description>`

### PR Description Template
**What's New?**
Briefly explain what was added or changed.

**Screenshots**
Add relevant screenshots or gifs if applicable.

**Related Issues**
Closes #ISSUE_NUMBER (if applicable).

---

## Step 10: Review and Merge
- **For contributors:** Wait for review, address changes, and a maintainer will merge.
- **For maintainers:** Review, comment, and use **Squash and Merge** for a clean history.

---

## Common Git Commands
| Command | Purpose |
| :--- | :--- |
| `git status` | Check the status of your files |
| `git add .` | Stage all changes |
| `git commit -m "message"` | Commit staged changes |
| `git push origin <branch>` | Push changes to GitHub |
| `git pull origin dev` | Pull latest changes from dev |
| `git branch` | List all branches |

## Tips and Best Practices
- **Always pull before you push.**
- **Use meaningful commit messages.**
- **One feature per branch.**
- **Test before you push.**
