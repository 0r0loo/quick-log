# Quick Log

![Build](https://img.shields.io/github/actions/workflow/status/0r0loo/quick-log/build.yml?branch=main)
![Version](https://img.shields.io/badge/version-0.0.1-blue)
![License](https://img.shields.io/badge/license-MIT-green)

*Read this in [Korean (한국어)](README_KO.md)*

<!-- Plugin description -->
Quick Log is a JetBrains IDE plugin that helps you insert styled console.log statements quickly. It supports multi-cursor functionality, includes file name and line number in output, and streamlines debugging for JavaScript/TypeScript developers.
<!-- Plugin description end -->

## Features

Quick Log plugin provides an easy way to add console.log statements for debugging JavaScript/TypeScript code:

- Auto-insert styled console.log statements after selecting a variable
- Include current file name and line number in the log output
- Use multi-cursor template when no text is selected
- Automatically maintain proper indentation

## Usage

1. Select the variable or text you want to log in your code.
2. Press the shortcut key `Alt+L` (default).
3. A console.log statement for the selected variable will be automatically inserted below the current line.

## Examples

When you select the variable `userName` and press the shortcut:

```javascript
const userName = "John";
console.log('fileName.js:2 | userName : ', userName);
```

When you press the shortcut without selecting anything:
```javascript
console.log('fileName.js:5 | ', );
```
In this state, multi-cursors are activated, allowing you to type text at label and value positions simultaneously.

## Installation

1. Search for "Quick Log" in the JetBrains IDE plugin marketplace.
2. Click the Install button.
3. Restart your IDE.

## Configuration

You can change the plugin shortcut in the IDE settings:

1. Go to `Settings/Preferences` > `Tools` > `Quick Log Settings`.
2. Enter your desired shortcut combination (e.g., `ctrl shift L`).
3. Save the settings and restart the IDE.

## System Requirements

- JetBrains IDEs: IntelliJ IDEA, WebStorm, PhpStorm, PyCharm, etc.
- IDE versions 242.* or higher (2024.2+)

## Contributing

Bug reports and feature requests are welcome through GitHub issues. Pull requests are also appreciated.

## License

This project is distributed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Developer Information

Made with ❤️ by [0r0loo](https://github.com/0r0loo)

## Changelog

For the latest changes, please refer to the [CHANGELOG.md](CHANGELOG.md) file.

## Additional Resources

- [Korean Documentation (한국어 문서)](README_KO.md)
