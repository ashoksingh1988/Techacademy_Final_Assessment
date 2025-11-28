# Testing SCM Polling Configuration

This is a dummy test file created to verify Jenkins SCM polling auto-trigger functionality.

## Test Information

- **Date**: 2025-11-29
- **Purpose**: Verify that Jenkins automatically detects Git commits and triggers techacademy-master-pipeline
- **Polling Interval**: Every 5 minutes

## Expected Behavior

When this file is committed and pushed to GitHub:
1. Jenkins will poll the repository within 5 minutes
2. Jenkins will detect the new commit
3. Only `techacademy-master-pipeline` will be auto-triggered
4. Master pipeline will execute selected frameworks

## Test Status

- [ ] File created
- [ ] Committed to Git
- [ ] Pushed to GitHub
- [ ] Jenkins auto-triggered
- [ ] Pipeline executed successfully

---

**Note**: This is a dummy file for testing purposes only.
