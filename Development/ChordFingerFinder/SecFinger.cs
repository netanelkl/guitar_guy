using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    class CSecFinger : CBasicGuitarFinger
    {
        public CSecFinger(CGuitarFinger SecondFinger)
            : base(SecondFinger)
        {

        }

        public CSecFinger(List<CGuitarFinger> SecondFinger)
            : base(SecondFinger)
        {

        }

        public CSecFinger()
            : base()
        {

        }

        public override int GetIndex()
        {
            return 2;
        }

        public static int CompareFinger(CSecFinger fst, CSecFinger sec)
        {
            if (fst.Priority == sec.Priority)
            {
                if (fst.Strings.Count > sec.Strings.Count)
                {
                    return -1;
                }
                else if (fst.Strings.Count < sec.Strings.Count)
                {
                    return 1;
                }
                else
                {
                    return fst.Strings[0] - sec.Strings[0];
                }
            }

            return sec.Priority - fst.Priority;
        }

        protected override bool GetPriorityForPlay(CFingerPosition cFngr, bool fCapo, out int nPriority)
        {
            nPriority = 0;
            if (!cFngr.IsValid())
            {
                return false;
            }

            int nResult = 0;
            nResult += (Globals.NUMBER_OF_FRETS - cFngr.Fret);

            if (fCapo)
            {
                nResult -= 4; // TODO: Change to be constants.
            }

            switch (cFngr.String)
            {
                case EString.Sixth:
                case EString.Fifth:
                case EString.Fourth:
                case EString.Third:
                    nResult += 5;
                    nResult -= ((EString.NumberOfStrings - cFngr.String) / 2);
                    break;
                case EString.Second:
                case EString.First:
                    nResult -= (3 + (int)cFngr.String);
                    break;
                default:
                    return false;
            }

            nPriority = nResult;
            return true;
        }

        public override bool CanPlay(CGuitarFinger cFngr)
        {
            if (cFngr.Strings.Count > 1)
            {
                if (cFngr.Strings.Contains(EString.Sixth) ||
                    cFngr.Strings.Contains(EString.Second) ||
                    cFngr.Strings.Contains(EString.First))
                {
                    return false;
                }
            }

            return base.CanPlay(cFngr);
        }
    };
}
