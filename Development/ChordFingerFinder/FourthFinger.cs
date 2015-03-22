using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    class CFourthFinger : CBasicGuitarFinger
    {
        public CFourthFinger(CGuitarFinger FourthFinger)
            : base(FourthFinger)
        {

        }

        public CFourthFinger(List<CGuitarFinger> FourthFinger)
            : base(FourthFinger)
        {

        }

        public CFourthFinger()
            : base()
        {

        }

        public override int GetIndex()
        {
            return 4;
        }

        protected override bool GetPriorityForPlay(CFingerPosition cFngr, bool fCapo, out int nPriority)
        {
            nPriority = 0;
            if (!cFngr.IsValid())
            {
                return false;
            }

            int nResult = 0;
            nResult += cFngr.Fret;

            if (fCapo)
            {
                nResult -= 10; // TODO: Change to be constants.
            }
            else
            {
                switch (cFngr.String)
                {
                    case EString.Sixth:
                    case EString.Fifth:
                    case EString.Fourth:
                    case EString.Third:
                        nResult -= ((EString.NumberOfStrings - cFngr.String) * 2);
                        break;
                    case EString.Second:
                    case EString.First:
                        nResult += 10;
                        break;
                    default:
                        throw new Exception("Damnit");
                }
            }

            nPriority = nResult;

            return true;
        }

        public override bool CanPlay(CGuitarFinger cFngr)
        {
            if (cFngr.Strings.Count > 1)
            {
                if (!(cFngr.Strings.Contains(EString.Second) ||
                      cFngr.Strings.Contains(EString.First)))
                {
                    return false;
                }
            }

            return base.CanPlay(cFngr);
        }
    };
}
