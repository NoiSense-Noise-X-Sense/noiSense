import js from '@eslint/js'
import globals from 'globals'
import react from 'eslint-plugin-react'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import * as tseslint from 'typescript-eslint'
import prettier from 'eslint-config-prettier'

export default tseslint.config(
  [
    {
      ignores: ['dist', 'build', 'node_modules'],
    },
    {
      files: ['**/*.{js,jsx,ts,tsx}'],
      languageOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
        parser: tseslint.parser,
        parserOptions: {
          project: './tsconfig.json',
          tsconfigRootDir: process.cwd(),
        },
        globals: {
          ...globals.browser,
          JSX: 'readonly',
        },
      },
      plugins: {
        react,
        'react-hooks': reactHooks,
        'react-refresh': reactRefresh,
      },
      rules: {
        ...js.configs.recommended.rules,
        ...tseslint.configs.recommended.rules,
        ...react.configs.recommended.rules,
        ...reactHooks.configs.recommended.rules,
        ...reactRefresh.configs.recommended.rules,

        // 커스터마이징 (필요하면 수정)
        'react/prop-types': 'off',
        'react/react-in-jsx-scope': 'off', // React 17+ JSX 자동 import
      },
      settings: {
        react: {
          version: 'detect',
        },
      },
    },
    {
      // Prettier 통합 (충돌 방지)
      rules: {
        ...prettier.rules,
      },
    },
  ]
)
