import moonIcon from '../assets/icons/moon.svg'
import sunIcon from '../assets/icons/sun.svg'

type SvgRepoIconName = 'moon' | 'sun'

type SvgRepoIconProps = {
  name: SvgRepoIconName
  className?: string
}

const icons = {
  moon: moonIcon,
  sun: sunIcon,
}

export function SvgRepoIcon({ name, className = 'h-5 w-5' }: SvgRepoIconProps) {
  return (
    <img
      src={icons[name]}
      alt=""
      aria-hidden="true"
      className={`ui-icon shrink-0 ${className}`}
    />
  )
}
