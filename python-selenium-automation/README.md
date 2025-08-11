# SauceDemo Python Automation

## Case Study Implementation

This framework implements the following requirements:

1. Launch URL using Selenium with Python: `https://www.saucedemo.com/`
2. Verify SWAG LABS is present on the Web Page
3. Add any one Item to cart (Click on ADD TO CART button)
4. Click on right corner button (shopping cart) and verify item is added to cart
5. Click on left corner button (menu) and click LOGOUT button

## Setup and Execution

### Prerequisites
- Python 3.7+
- Chrome browser installed

### Installation
```bash
cd python_automation
pip install -r requirements.txt
```

### Running Tests
```bash
# Run all tests
pytest tests/ -v

# Run specific test
pytest tests/test_saucedemo_case_study.py -v

# Run with HTML report
pytest tests/ -v --html=reports/report.html --self-contained-html
```

### Using Batch File
```bash
run_tests.bat
```

## Framework Structure

- `pages/` - Page Object Model classes with generic functions
- `tests/` - Test cases using pytest framework
- `conftest.py` - pytest fixtures and configuration
- `requirements.txt` - Python dependencies
