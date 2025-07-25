import React, { forwardRef } from 'react';
import { CalendarIcon } from 'lucide-react';

interface CustomInputProps {
  value?: string;
  onClick?: () => void;
  placeholder?: string;
}

const CustomDatePickerInput = forwardRef<HTMLButtonElement, CustomInputProps>(
  ({ value, onClick, placeholder }, ref) => (
    <div className="relative w-full">
      <button
        type="button"
        className="w-full cursor-pointer pl-3 pr-10 py-2 text-left border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 hover:border-blue-400 bg-white"
        onClick={onClick}
        ref={ref}
      >
        {value || <span className="text-gray-500">{placeholder}</span>}
      </button>
      <CalendarIcon
        className="absolute right-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400 pointer-events-none"
      />
    </div>
  )
);

CustomDatePickerInput.displayName = 'CustomDatePickerInput';

export default CustomDatePickerInput;
