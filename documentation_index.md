# Archived

Use these entry points instead:
- Onboarding and setup: [README.md](README.md)
- Backend setup and endpoints: [spring-api/SETUP.md](spring-api/SETUP.md)
- IDE setup: [INTELLIJ_SETUP.md](INTELLIJ_SETUP.md)

This index is deprecated to reduce duplication and remove local paths.

1. **Review Roadmap:** Check [ROADMAP.md](ROADMAP.md) for available tasks
2. **Assign Task:** Pick a feature from Phase 1, 2, or 3
3. **Create Branch:** `git checkout -b feature/your-feature-name`
4. **Implement:** Follow code style and patterns
5. **Test:** Add unit tests for your code
6. **Document:** Update relevant markdown files
7. **Submit PR:** Include test results and documentation updates

### Code Style Guidelines
- Use camelCase for variables and methods
- Use PascalCase for class names
- Add Javadoc comments for public methods
- Keep methods focused (single responsibility)
- Use meaningful variable names
- Follow design patterns (don't reinvent the wheel)

### Testing Expectations
- Write tests for new features
- Aim for 70%+ code coverage
- Use JUnit 5 format (see JUNIT_SETUP.md)
- Include edge cases and error scenarios

---

## 🐛 Troubleshooting

### Common Issues

**Q: "Package does not exist" error during compilation**  
A: Ensure all JARs are in `./lib/` and classpath includes them. See SETUP.md for fix.

**Q: Tests don't compile**  
A: Download JUnit 5 JARs. See JUNIT_SETUP.md for detailed instructions.

**Q: Configuration not found**  
A: Ensure `./configuration/app.properties` exists. Default paths should work.

**Q: Report export fails**  
A: Ensure `./output/` directory exists and has write permissions.

**Q: Tax calculations don't match previous run**  
A: Check if different Excel file was used. Use `./configuration/tax_2122_.xlsx` for baseline.

### Getting Help
1. Check the [JUNIT_SETUP.md](JUNIT_SETUP.md#common-setup-issues) troubleshooting section
2. Review [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md#support--maintenance) for solutions
3. Check inline code comments for implementation details
4. Create a GitHub issue with detailed error description

---

## 📞 Contact & Support

### Documentation Maintainers
- Architecture: See IMPLEMENTATION_SUMMARY.md
- Testing: See JUNIT_SETUP.md
- Setup: See SETUP.md

### Version Information
- **Project Version:** 2.0 (Post-Enhancement)
- **Java Version:** 21
- **Last Updated:** Q1 2024
- **Release Cycle:** Quarterly

---

## 📝 Document Versions

| Document | Version | Last Updated | Status |
|----------|---------|--------------|--------|
| SETUP.md | 1.0 | Q1 2024 | ✅ Complete |
| README.md | 2.0 | Q1 2024 | ✅ Complete |
| IMPROVEMENTS.md | 1.0 | Q1 2024 | ✅ Complete |
| ROADMAP.md | 1.0 | Q1 2024 | ✅ Complete |
| JUNIT_SETUP.md | 1.0 | Q1 2024 | ✅ Complete |
| IMPLEMENTATION_SUMMARY.md | 1.0 | Q1 2024 | ✅ Complete |
| documentation_index.md | 1.0 | Q1 2024 | ✅ Complete |

---

## 🎓 Learning Resources

### For Java Developers
- [Java 8 LocalDate Documentation](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)
- [Design Patterns in Java](https://www.baeldung.com/design-patterns-in-java)
- [Apache POI Excel API](https://poi.apache.org/)

### For Tax Specialists
- [India Income Tax Rules](https://www.incometaxindia.gov.in/)
- [Capital Gains Tax in India](https://www.investopedia.com/terms/c/capital-gains.asp)
- [STCG vs LTCG](https://www.taxovation.com/stcg-vs-ltcg-difference/)

### For DevOps
- [Java Environment Setup](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)
- [Maven Build Automation](https://maven.apache.org/)
- [Git Version Control](https://git-scm.com/)

---

## ✅ Next Steps

Choose your role and proceed:

**👨‍💼 Project Manager**
→ Review [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) and [ROADMAP.md](ROADMAP.md)

**👨‍💻 Developer**
→ Follow [SETUP.md](SETUP.md) to get environment ready

**🧪 QA Engineer**
→ Follow [JUNIT_SETUP.md](JUNIT_SETUP.md) to set up tests

**📊 Data Analyst**
→ Explore output files: `./output/*.csv` and `./output/*.json`

**🔧 DevOps Engineer**
→ Review [SETUP.md](SETUP.md#deployment-checklist) for deployment instructions

---

**Happy coding! 🚀**

For questions or issues, refer to the relevant documentation or create a GitHub issue.
